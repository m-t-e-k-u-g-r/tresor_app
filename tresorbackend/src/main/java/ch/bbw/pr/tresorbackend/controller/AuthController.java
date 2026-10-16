package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.dto.AuthResponse;
import ch.bbw.pr.tresorbackend.dto.CustomUserDetails;
import ch.bbw.pr.tresorbackend.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
}
