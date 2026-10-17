package ch.bbw.pr.tresorbackend;

import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.repository.UserRoleRepository;
import ch.bbw.pr.tresorbackend.service.PasswordEncryptService;
import ch.bbw.pr.tresorbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@AllArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {
    private final Environment env;
    private final UserService userService;
    private final PasswordEncryptService passwordEncryptService;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(ApplicationArguments args) {
        User admin = userService.getUserById(1);
        if (admin != null) return;

        String adminKey = "app.initial-admin";
        String password = env.getProperty(adminKey + ".password");
        String hashed = passwordEncryptService.encode(password);

        byte[] salt = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        User user = User.builder()
                .email(env.getProperty(adminKey + ".email"))
                .firstName(env.getProperty(adminKey + ".first-name"))
                .lastName(env.getProperty(adminKey + ".last-name"))
                .password(hashed)
                .salt(Base64.getEncoder().encodeToString(salt))
                .userRole(userRoleRepository.findByRole("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Failed to get admin role")))
                .build();

        User created = userService.createUser(user);
        if (created == null) throw new RuntimeException("Failed to save admin user");
    }
}
