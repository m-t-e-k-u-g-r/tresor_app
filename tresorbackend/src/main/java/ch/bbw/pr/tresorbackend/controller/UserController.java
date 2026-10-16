package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.dto.*;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.mappers.UserMapper;
import ch.bbw.pr.tresorbackend.repository.UserRoleRepository;
import ch.bbw.pr.tresorbackend.service.PasswordEncryptService;
import ch.bbw.pr.tresorbackend.service.UserService;

import ch.bbw.pr.tresorbackend.util.ErrorHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * UserController
 * @author Peter Rutschmann
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/users")
public class UserController {

   private final ErrorHandler errorHandler;
   private final UserMapper userMapper;
   private final AuthenticationManager authenticationManager;
   private final UserService userService;
   private final PasswordEncryptService passwordService;
   private final SecurityContextHolderStrategy securityContextHolderStrategy =
           SecurityContextHolder.getContextHolderStrategy();
   private final SecurityContextRepository securityContextRepository;
   private final UserRoleRepository userRoleRepository;

   // build create User REST API
   @PostMapping("new")
   public ResponseEntity<String> createUser(
           @Valid @RequestBody RegisterUser registerUser,
           BindingResult bindingResult
   ) {
      //captcha
      //todo add implementation

      log.info("UserController.createUser: captcha passed.");

      //input validation
      if (bindingResult.hasErrors()) return errorHandler.handleErrors(bindingResult, "UserController.createUser");
      log.info("UserController.createUser: input validation passed");

      log.info("UserController.createUser, password validation passed");

      User user = userMapper.toEntity(registerUser);
      byte[] salt = new byte[32];
      SecureRandom secureRandom = new SecureRandom();
      secureRandom.nextBytes(salt);

      user.setSalt(Base64.getEncoder().encodeToString(salt));
      user.setUserRole(userRoleRepository.findById(2).orElseThrow());
      try {
         user.setPassword(passwordService.encode(registerUser.getPassword()));
      } catch (Exception e) {
         log.error("UserController.createUser failed to hash password");
         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
      }
      User savedUser = userService.createUser(user);
      JsonObject obj = new JsonObject();
      if (savedUser != null) {
         log.info("UserController.createUser, user saved in db");
         obj.addProperty("answer", "User saved");
      } else {
         log.info("UserController.createUser, user not saved in db");
         obj.addProperty("answer", "User not saved");
      }
      String json = new Gson().toJson(obj);
      log.info("UserController.createUser {}", json);
      return ResponseEntity.accepted().body(json);
   }

   // build get user by id REST API
   // http://localhost:8080/api/users/1
   @GetMapping("{id}")
   @PreAuthorize("@authz.isAdminOrOwner(#userId, authentication)")
   public ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer userId) {
      User user = userService.getUserById(userId);
      if (user == null) return ResponseEntity.notFound().build();
      return ResponseEntity.ok(userMapper.toDto(user));
   }

   // Build Get All Users REST API
   // http://localhost:8080/api/users/all
   @GetMapping("all")
   public ResponseEntity<List<UserDto>> getAllUsers() {
      List<UserDto> users = userService.getAllUsers()
              .stream().map(userMapper::toDto)
              .toList();
      if (users.isEmpty()) return ResponseEntity.notFound().build();
      return new ResponseEntity<>(users, HttpStatus.OK);
   }

   // Build Update User REST API
   // http://localhost:8080/api/users/1
   @PutMapping("{id}")
   @PreAuthorize("@authz.isAdminOrOwner(#userId, authentication)")
   public ResponseEntity<UserDto> updateUser(
           @PathVariable("id") Integer userId,
           @RequestBody UserDto userDto
   ) {
      User updatedUser = userService.updateUser(userId, userDto);
      if (updatedUser == null) return ResponseEntity.notFound().build();
      return ResponseEntity.ok(userMapper.toDto(updatedUser));
   }

   // Build Delete User REST API
   @DeleteMapping("{id}")
   @PreAuthorize("@authz.isAdminOrOwner(#userId, authentication)")
   public ResponseEntity<String> deleteUser(@PathVariable("id") Integer userId) {
      if( userService.deleteUser(userId))
         return new ResponseEntity<>("User successfully deleted!", HttpStatus.OK);
      return ResponseEntity.notFound().build();
   }

   // get user id by email
   @PostMapping("/byemail")
   public ResponseEntity<String> getUserIdByEmail(@RequestBody EmailAddress email, BindingResult bindingResult) {
      log.info("UserController.getUserIdByEmail: {}", email);
      //input validation
      if (bindingResult.hasErrors()) errorHandler.handleErrors(bindingResult, "UserController.getUserIdByEmail");

      log.info("UserController.getUserIdByEmail: input validation passed");

      User user = userService.findByEmail(email.getEmail());
      if (user == null) {
         log.info("UserController.getUserIdByEmail, no user found with email: {}", email);
         String json = new Gson().toJson(Map.of("message", "No user found with this email"));
          log.info("UserController.getUserIdByEmail, fails: {}", json);
         return ResponseEntity.badRequest().body(json);
      }

      log.info("UserController.getUserIdByEmail, user find by email");
      JsonObject obj = new JsonObject();
      obj.addProperty("answer", user.getId());
      String json = new Gson().toJson(obj);
      log.info("UserController.getUserIdByEmail {}", json);
      return ResponseEntity.accepted().body(json);
   }

   // simple login with no websecurity, just name and password
   @PostMapping("/login")
   public ResponseEntity<LoginResponse> doLoginUser(
           HttpServletRequest request,
           HttpServletResponse response,
           @RequestBody LoginUser loginUser
   ) {
      log.info("UserController.doLoginUser: {}", loginUser);

      UsernamePasswordAuthenticationToken authReq =
              new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword());

      Authentication authResult = authenticationManager.authenticate(authReq);

      SecurityContext context = securityContextHolderStrategy.createEmptyContext();
      context.setAuthentication(authResult);
      securityContextHolderStrategy.setContext(context);

      securityContextRepository.saveContext(context, request, response);

      User user = userService.findByEmail(loginUser.getEmail());
      log.info("UserController.doLoginUser: login successful");
      log.info("UserController.doLoginUser: {}", context.getAuthentication().getAuthorities().stream().findFirst());
      boolean isAdmin = context.getAuthentication().getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
      return ResponseEntity.ok(new LoginResponse("Login successful", user.getId(), isAdmin));
   }

}
