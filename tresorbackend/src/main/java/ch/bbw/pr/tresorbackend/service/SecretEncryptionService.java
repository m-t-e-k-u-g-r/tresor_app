package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.dto.CryptOperation;
import ch.bbw.pr.tresorbackend.dto.SecretDto;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@AllArgsConstructor
public class SecretEncryptionService {
    public SecretDto encryptSecret(SecretDto secret, SecretKey key) throws RuntimeException {
        return handleSecret(secret, key, CryptOperation.ENCRYPTION);
    }

    public SecretDto decryptSecret(SecretDto secret, SecretKey key) throws RuntimeException {
        return handleSecret(secret, key, CryptOperation.DECRYPTION);
    }

    private SecretDto handleSecret(SecretDto secret, SecretKey key, CryptOperation operation) throws RuntimeException {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(key);
            return switch (operation) {
                case ENCRYPTION -> secret.transform(encryptUtil::encrypt);
                case DECRYPTION -> secret.transform(encryptUtil::decrypt);
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
