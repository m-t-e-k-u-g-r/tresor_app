package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.dto.CryptOperation;
import ch.bbw.pr.tresorbackend.dto.SecretDto;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import org.springframework.stereotype.Service;

@Service
public class SecretEncryptionService {
    public SecretDto encryptSecret(SecretDto secrets) {
        return handleSecret(secrets, CryptOperation.ENCRYPTION);
    }

    public SecretDto decryptSecret(SecretDto secret) {
        return handleSecret(secret, CryptOperation.DECRYPTION);
    }

    private SecretDto handleSecret(SecretDto secret, CryptOperation operation) {
        try {
            return switch (operation) {
                case ENCRYPTION -> secret.transform(EncryptUtil::encrypt);
                case DECRYPTION -> secret.transform(EncryptUtil::decrypt);
            };
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
