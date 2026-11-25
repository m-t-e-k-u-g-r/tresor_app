package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.dto.AuthResponse;
import ch.bbw.pr.tresorbackend.dto.ChangePasswordRequest;
import ch.bbw.pr.tresorbackend.dto.CustomUserDetails;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.repository.UserRepository;
import ch.bbw.pr.tresorbackend.service.KeyService;
import ch.bbw.pr.tresorbackend.service.PasswordEncryptService;
import ch.bbw.pr.tresorbackend.service.SecretService;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import ch.bbw.pr.tresorbackend.util.SaltUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncryptService passwordEncryptService;
    private final KeyService keyService;
    private final SaltUtil saltUtil;
    private final SecretService secretService;
    private final UserRepository userRepository;

    @GetMapping("/check")
    public ResponseEntity<AuthResponse> checkAuth(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        AuthResponse response = new AuthResponse(user.getId(), user.getEmail(), isAdmin, null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        if (request.oldPassword().equals(request.newPassword())) return ResponseEntity.badRequest().build();
        User user = userService.findByEmail(request.email());
        if (user == null) return ResponseEntity.notFound().build();

        if (!passwordEncryptService.matches(request.oldPassword(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        log.debug("Old password matches");

        String newPasswordHash = passwordEncryptService.encode(request.newPassword());

        if (user.getEncryptedDek() == null) {
            user.setPassword(newPasswordHash);
            keyService.setDEK(user, request.newPassword());
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }

        SecretKey dek = keyService.getDEK(request.oldPassword(), user.getSalt(), user.getEncryptedDek());
        log.debug("Encryption key retrieved");
        String encodedDEK = Base64.getEncoder().encodeToString(dek.getEncoded());

        byte[] newSalt = saltUtil.generateSalt();
        String saltString = Base64.getEncoder().encodeToString(newSalt);
        EncryptUtil encryptUtil = new EncryptUtil(keyService.deriveKey(request.newPassword(), saltString));
        log.debug("New encryption key derived");

        user.setEncryptedDek(encryptUtil.encrypt(encodedDEK));
        user.setSalt(saltString);
        user.setPassword(newPasswordHash);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<Integer> deleteAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userRepository.delete(userDetails.getUser());

        int deleted = secretService.purgeSecretsByUserId(userDetails.getUser().getId());

        return ResponseEntity.ok(deleted);
    }
}
