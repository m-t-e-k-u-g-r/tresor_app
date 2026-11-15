package ch.bbw.pr.tresorbackend.util;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * SaltUtil
 *
 * @author Matteo Krüger
 */
@Service
public class SaltUtil {

    public byte[] generateSalt() {
        byte[] salt = new byte[32];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
