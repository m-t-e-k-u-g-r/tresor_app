package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


/**
 * KeyUtil
 *
 * @author Matteo Krüger
 */
@Service
@AllArgsConstructor
public class KeyService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public SecretKey getRandomKey() {
        byte[] passwordBytes = new byte[32];
        byte[] saltBytes = new byte[16];

        SECURE_RANDOM.nextBytes(passwordBytes);
        SECURE_RANDOM.nextBytes(saltBytes);

        String password = Base64.getEncoder().encodeToString(passwordBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);

        return deriveKey(password, salt);
    }

    public SecretKey deriveKey(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    500_000,
                    256
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derivedKey = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(derivedKey, "AES");
        } catch (IllegalArgumentException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public SecretKey getEncryptionKey(String password, String salt, String encryptedKey) {
        SecretKey encryptionKey = deriveKey(password, salt);
        EncryptUtil encryptUtil = new EncryptUtil(encryptionKey);
        String encodedKey = encryptUtil.decrypt(encryptedKey);
        return new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES");
    }
}
