package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.dto.CustomUserDetails;
import ch.bbw.pr.tresorbackend.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {

    public boolean isAdminOrOwner(Integer userId, Authentication authentication) {
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        User user = userDetails.getUser();

        return isAdmin || user.getId().equals(userId);
    }
}
