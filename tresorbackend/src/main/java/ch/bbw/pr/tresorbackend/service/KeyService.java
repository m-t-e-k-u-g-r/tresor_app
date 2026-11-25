package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


/**
 * KeyUtil
 *
 * @author Matteo Krüger
 */
@Slf4j
@Service
@AllArgsConstructor
public class KeyService {
    public SecretKey getRandomKey() {
        return Jwts.SIG.HS256.key().build();
    }

    // Derives a key from a password and salt using PBKDF2WithHmacSHA256
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

    // Decrypts the encrypted DEK stored in the DB
    public SecretKey getDEK(String password, String salt, String encryptedDEK) {
        SecretKey kek = deriveKey(password, salt);
        log.debug("Key encryption key derived");
        EncryptUtil encryptUtil = new EncryptUtil(kek);
        log.debug("EncryptUtil instantiated");
        String encodedDEK = encryptUtil.decrypt(encryptedDEK);
        log.debug("Data encryption key decrypted");
        return decodeKey(encodedDEK);
    }

    // Set Data Encryption Key (DEK) for User
    public void setDEK(User user, String password) {
        String saltString = user.getSalt();

        byte[] dek = getRandomKey().getEncoded();
        String encodedDEK = Base64.getEncoder().encodeToString(dek);
        SecretKey kek = deriveKey(password, saltString);
        EncryptUtil encryptUtil = new EncryptUtil(kek);
        String encryptedDEK = encryptUtil.encrypt(encodedDEK);
        user.setEncryptedDek(encryptedDEK);
    }

    public String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public SecretKey decodeKey(String encodedKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES");
    }
}
