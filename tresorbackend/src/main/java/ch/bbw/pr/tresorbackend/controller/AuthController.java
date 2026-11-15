package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.dto.AuthResponse;
import ch.bbw.pr.tresorbackend.dto.ChangePasswordRequest;
import ch.bbw.pr.tresorbackend.dto.CustomUserDetails;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.repository.UserRepository;
import ch.bbw.pr.tresorbackend.service.KeyService;
import ch.bbw.pr.tresorbackend.service.PasswordEncryptService;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import ch.bbw.pr.tresorbackend.util.SaltUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncryptService passwordEncryptService;
    private final KeyService keyService;
    private final SaltUtil saltUtil;
    private final UserRepository userRepository;

    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/check")
    public ResponseEntity<AuthResponse> checkAuth(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        AuthResponse response = new AuthResponse(user.getId(), isAdmin);

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

        SecretKey key = keyService.getEncryptionKey(request.oldPassword(), user.getSalt(), user.getEncryptedKey());
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        byte[] newSalt = saltUtil.generateSalt();
        String saltString = Base64.getEncoder().encodeToString(newSalt);
        EncryptUtil encryptUtil = new EncryptUtil(keyService.deriveKey(request.newPassword(), saltString));

        user.setEncryptedKey(encryptUtil.encrypt(encodedKey));
        user.setSalt(saltString);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
