package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.config.EncryptionProperties;
import ch.bbw.pr.tresorbackend.dto.CryptOperation;
import ch.bbw.pr.tresorbackend.dto.SecretDto;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class SecretEncryptionService {
    private final EncryptionProperties encryptionProperties;

    public SecretEncryptionService(EncryptionProperties encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
    }

    public SecretDto encryptSecret(SecretDto secret, String salt) throws RuntimeException {
        return handleSecret(secret, salt, CryptOperation.ENCRYPTION);
    }

    public SecretDto decryptSecret(SecretDto secret, String salt) throws RuntimeException {
        return handleSecret(secret, salt, CryptOperation.DECRYPTION);
    }

    private SecretDto handleSecret(SecretDto secret, String salt, CryptOperation operation) throws RuntimeException {
        try {
            String masterKey = encryptionProperties.getMasterKey();
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(
                    masterKey.toCharArray(),
                    saltBytes,
                    500_000,
                    256
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derivedKey = factory.generateSecret(spec).getEncoded();
            SecretKey aesKey = new SecretKeySpec(derivedKey, "AES");

            EncryptUtil encryptUtil = new EncryptUtil(aesKey);
            return switch (operation) {
                case ENCRYPTION -> secret.transform(encryptUtil::encrypt);
                case DECRYPTION -> secret.transform(encryptUtil::decrypt);
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
