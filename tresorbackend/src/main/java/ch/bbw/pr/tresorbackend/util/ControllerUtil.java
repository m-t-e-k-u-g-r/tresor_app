package ch.bbw.pr.tresorbackend.util;

import ch.bbw.pr.tresorbackend.service.PasswordEncryptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ControllerUtil {
    private final PasswordEncryptService passwordEncryptService;

    public ControllerUtil(PasswordEncryptService passwordEncryptService) {
        this.passwordEncryptService = passwordEncryptService;
    }

    public void validatePassword(String providedPassword, String savedPassword, String controllerMethod) {
        boolean valid;
        try {
            valid = passwordEncryptService.matches(providedPassword, savedPassword);
        } catch (Exception e) {
            log.error("{} Failed to check password {}", controllerMethod, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!valid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
