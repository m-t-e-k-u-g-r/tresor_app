package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.dto.*;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.service.SecretEncryptionService;
import ch.bbw.pr.tresorbackend.service.SecretService;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.util.ErrorHandler;
import com.google.gson.Gson;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SecretController
 * @author Peter Rutschmann
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/secrets")
public class SecretController {

   private final UserService userService;
   private SecretService secretService;
   private SecretEncryptionService encryptionService;
   private ErrorHandler errorHandler;

   // create secret REST API
   @PostMapping("/new")
   public ResponseEntity<String> createSecret2(
           @Valid @RequestBody SecretDto secretDto,
           @AuthenticationPrincipal CustomUserDetails userDetails,
           BindingResult bindingResult
   ) {
      //input validation
      if (bindingResult.hasErrors()) return errorHandler.handleErrors(bindingResult, "SecretController.createSecret2");
      log.info("SecretController.createSecret, input validation passed");

      User user = userDetails.getUser();
      SecretDto encrypted = encryptionService.encryptSecret(secretDto, user.getSalt());
      secretService.createSecret(user, encrypted);

      log.info("SecretController.createSecret, secret saved in db");
      String json = new Gson().toJson(Map.of("answer", "Secret saved"));
      log.info("SecretController.createSecret {}", json);
      return ResponseEntity.accepted().body(json);
   }

   // Build Get Secrets by userId REST API
   @GetMapping("/byuserid/{userId}")
   @PreAuthorize("@authz.isAdminOrOwner(#userId, authentication)")
   public ResponseEntity<List<SecretDto>> getSecretsByUserId(@PathVariable Integer userId) {
      User user = userService.getUserById(userId);
      if (user == null) return ResponseEntity.notFound().build();

      List<SecretDto> secrets = secretService.getSecretsByUserId(user.getId());
      if (secrets.isEmpty()) {
         log.info("SecretController.getSecretsByUserId secret isEmpty");
         return ResponseEntity.notFound().build();
      }
      List<SecretDto> decrypted = secrets
              .stream().map(s -> encryptionService.decryptSecret(s, user.getSalt()))
              .toList();
      return ResponseEntity.ok(decrypted);
   }

   // Build Get Secrets by email REST API
   @GetMapping
   public ResponseEntity<List<SecretDto>> getSecrets(
           @AuthenticationPrincipal CustomUserDetails userDetails
   ) {
      User user = userDetails.getUser();
      List<SecretDto> secrets = secretService.getSecretsByUserId(user.getId());
      if (secrets.isEmpty()) {
         log.info("SecretController.getSecretsByEmail no stored secret");
         return ResponseEntity.noContent().build();
      }
      try {
         List<SecretDto> decrypted = secrets
                 .stream().map(s -> encryptionService.decryptSecret(s, user.getSalt()))
                 .toList();
         return ResponseEntity.ok(decrypted);
      } catch (Exception e) {
         log.error("SecretController.getSecretsByEmail: {}", e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }

   // Build Get All Secrets REST API
   // http://localhost:8080/api/secrets/all
   @GetMapping("/all")
   public ResponseEntity<List<SecretDto>> getAllSecrets() {
      List<SecretDto> secrets = secretService.getAllSecrets();
      if (secrets.isEmpty()) return ResponseEntity.noContent().build();
      return ResponseEntity.ok(secrets);
   }

   // Build Update Secrete REST API
   // http://localhost:8080/api/secrets/NOTE/1
   @PutMapping("/{type}/{id}")
   public ResponseEntity<String> updateSecret(
           @PathVariable("type") String typeString,
           @PathVariable("id") Long secretId,
           @Valid @RequestBody SecretDto secretDto,
           @AuthenticationPrincipal CustomUserDetails userDetails,
           BindingResult bindingResult
   ) {
      //input validation
      if (bindingResult.hasErrors()) return errorHandler.handleErrors(bindingResult, "SecretController.updateSecret");

      SecretType type = SecretType.parseSecretType(typeString);
      if (type == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid secret type");

      User user = userDetails.getUser();
      SecretDto dbSecret = secretService.getSecretByIdAndUserId(type, secretId, user.getId());
      if(dbSecret == null){
         log.info("SecretController.updateSecret, secret not found in db");
         String json = new Gson().toJson(Map.of("answer", "Secret not found in db"));
         log.info("SecretController.updateSecret failed:{}", json);
         return ResponseEntity.badRequest().body(json);
      }
      try {
         encryptionService.decryptSecret(dbSecret, user.getSalt());
      } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      secretService.updateSecretByIdAndUserId(type, secretId, user.getId(), secretDto);

      log.info("SecretController.updateSecret, secret updated in db");
      String json = new Gson().toJson(Map.of("answer", "Secret updated"));
      log.info("SecretController.updateSecret {}", json);
      return ResponseEntity.accepted().body(json);
   }

   // Build Delete Secret REST API
   @DeleteMapping("/{type}/{id}")
   public ResponseEntity<String> deleteSecret(
           @PathVariable("type") String typeString,
           @PathVariable("id") Long secretId,
           @AuthenticationPrincipal CustomUserDetails userDetails
   ) {
      SecretType type = SecretType.parseSecretType(typeString);
      if (type == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid secret type");

      User user = userDetails.getUser();
      secretService.deleteSecretByIdAndUserId(type, secretId, user.getId());
      log.info("SecretController.deleteSecret successfully: {}", secretId);
      return ResponseEntity.ok("Secret successfully deleted!");
   }
}
