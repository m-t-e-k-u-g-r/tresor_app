package ch.bbw.pr.tresorbackend.config;

import ch.bbw.pr.tresorbackend.dto.CustomUserDetails;
import ch.bbw.pr.tresorbackend.service.JwtService;
import ch.bbw.pr.tresorbackend.service.KeyService;
import ch.bbw.pr.tresorbackend.service.impl.UserServiceImpl;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

/**
 * JwtFilter
 *
 * @author Matteo Krüger
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final EncryptUtil encryptUtil;
    private final KeyService keyService;
    private final UserServiceImpl userService;
    private final JwtService jwtService;

    public JwtFilter(EncryptionProperties encryptionProperties, UserServiceImpl userService, KeyService keyService, JwtService jwtService) {
        this.encryptUtil = new EncryptUtil(keyService.decodeKey(encryptionProperties.getMasterKey()));
        this.userService = userService;
        this.keyService = keyService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info(request.getRequestURL().toString());
        String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Token: {}", token);

            if (jwtService.validateToken(token)) {
                String email = jwtService.extractEmail(token);
                log.info("Email: {}", email);
                CustomUserDetails userDetails = userService.loadUserByUsername(email);

                String encryptedKEK = jwtService.extractEncryptedKEK(token);
                log.info("Encrypted KEK: {}", encryptedKEK);
                SecretKey kek = keyService.decodeKey(encryptUtil.decrypt(encryptedKEK));

                String encodedDEK = new EncryptUtil(kek).decrypt(userDetails.getUser().getEncryptedDek());
                log.info("Encoded DEK: {}", encodedDEK);
                SecretKey dek = keyService.decodeKey(encodedDEK);

                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, dek, userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.info("Token is invalid");
            }
        } else {
            log.info("Authorization header is missing or invalid");
        }
        filterChain.doFilter(request, response);
    }
}
