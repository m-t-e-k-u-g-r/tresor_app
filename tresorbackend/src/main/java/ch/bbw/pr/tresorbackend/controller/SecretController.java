package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.dto.CreateSecretRequest;
import ch.bbw.pr.tresorbackend.dto.EncryptCredentials;
import ch.bbw.pr.tresorbackend.dto.SecretDto;
import ch.bbw.pr.tresorbackend.dto.SecretType;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.service.PasswordEncryptService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SecretController
 * @author Peter Rutschmann
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/secrets")
public class SecretController {

   private final PasswordEncryptService passwordEncryptService;
   private SecretService secretService;
   private SecretEncryptionService encryptionService;
   private ErrorHandler errorHandler;
   private UserService userService;

   // create secret REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/{type}")
   public ResponseEntity<String> createSecret2(
           @PathVariable SecretType type,
           @Valid @RequestBody CreateSecretRequest<? extends SecretDto> createSecretRequest,
           BindingResult bindingResult
   ) {
      //input validation
      if (bindingResult.hasErrors()) return errorHandler.handleErrors(bindingResult, "SecretController.createSecret2");
      log.info("SecretController.createSecret, input validation passed");

      User user = userService.findByEmail(createSecretRequest.getEmail());
      if (user == null) return ResponseEntity.notFound().build();

      SecretDto encrypted = encryptionService.encryptSecret(
              createSecretRequest.getSecret(),
              user.getSalt()
      );
      secretService.createSecret(user, type, encrypted);

      log.info("SecretController.createSecret, secret saved in db");
      String json = new Gson().toJson(Map.of("answer", "Secret saved"));
      log.info("SecretController.createSecret {}", json);
      return ResponseEntity.accepted().body(json);
   }

   // Build Get Secrets by userId REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/byuserid")
   public ResponseEntity<List<SecretDto>> getSecretsByUserId(@RequestBody EncryptCredentials credentials) {
      if (credentials.getUserId() == null) return ResponseEntity.badRequest().build();
      log.info("SecretController.getSecretsByUserId {}", credentials);

      User user = userService.getUserById(credentials.getUserId());
      if (user == null) return ResponseEntity.notFound().build();
      boolean valid = passwordEncryptService.CheckPassword(credentials.getPassword(), user.getPassword());
      if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/byemail")
   public ResponseEntity<List<SecretDto>> getSecretsByEmail(
           @RequestBody EncryptCredentials credentials
   ) {
      if (credentials.getEmail() == null) return ResponseEntity.badRequest().build();
      log.info("SecretController.getSecretsByEmail {}", credentials);

      User user = userService.findByEmail(credentials.getEmail());
      if (user == null) return ResponseEntity.notFound().build();
      boolean valid = passwordEncryptService.CheckPassword(credentials.getPassword(), user.getPassword());
      if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

      List<SecretDto> secrets = secretService.getSecretsByUserId(user.getId());
      if (secrets.isEmpty()) {
         log.info("SecretController.getSecretsByEmail secret isEmpty");
         return ResponseEntity.notFound().build();
      }
      try {
         List<SecretDto> decrypted = secrets
                 .stream().map(s -> encryptionService.decryptSecret(s, user.getSalt()))
                 .toList();
         return ResponseEntity.ok(decrypted);
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }

   // Build Get All Secrets REST API
   // http://localhost:8080/api/secrets
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @GetMapping
   public ResponseEntity<List<SecretDto>> getAllSecrets() {
      List<SecretDto> secrets = secretService.getAllSecrets();
      return new ResponseEntity<>(secrets, HttpStatus.OK);
   }

   // Build Update Secrete REST API
   // http://localhost:8080/api/secrets/1
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PutMapping("/{type}/{id}")
   public ResponseEntity<String> updateSecret(
           @PathVariable SecretType type,
           @PathVariable("id") Long secretId,
           @Valid @RequestBody CreateSecretRequest<? extends SecretDto> createSecretRequest,
           BindingResult bindingResult
   ) {
      //input validation
      if (bindingResult.hasErrors()) return errorHandler.handleErrors(bindingResult, "SecretController.updateSecret");

      User user = userService.findByEmail(createSecretRequest.getEmail());
      if (user == null) return ResponseEntity.notFound().build();
      boolean valid = passwordEncryptService.CheckPassword(createSecretRequest.getPassword(), user.getPassword());
      if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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
      secretService.updateSecretByIdAndUserId(type, secretId, user.getId(), createSecretRequest.getSecret());

      log.info("SecretController.updateSecret, secret updated in db");
      String json = new Gson().toJson(Map.of("answer", "Secret updated"));
      log.info("SecretController.updateSecret {}", json);
      return ResponseEntity.accepted().body(json);
   }

   // Build Delete Secret REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @DeleteMapping("/{type}/{id}")
   public ResponseEntity<String> deleteSecret(
           @PathVariable("type") SecretType type,
           @PathVariable("id") Long secretId,
           @RequestBody EncryptCredentials credentials
   ) {
      //todo: Some kind of brute force delete, perhaps test first userid and encryptpassword
      User user = userService.findByEmail(credentials.getEmail());
      if (user == null || !Objects.equals(credentials.getUserId(), user.getId())) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }

      secretService.deleteSecretByIdAndUserId(type, secretId, user.getId());
      log.info("SecretController.deleteSecret successfully: {}", secretId);
      return ResponseEntity.ok("Secret successfully deleted!");
   }
}
