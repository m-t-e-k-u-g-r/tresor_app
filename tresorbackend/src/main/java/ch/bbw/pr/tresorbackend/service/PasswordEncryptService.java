package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.config.EncryptionProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * PasswordEncryptService
 *   used to hash password and verify match
 * @author Peter Rutschmann
 */
@Service
public class PasswordEncryptService {
   private final BCryptPasswordEncoder bcryptEncoder;
   private final EncryptionProperties encryptionProperties;

   public PasswordEncryptService(BCryptPasswordEncoder bcryptEncoder, EncryptionProperties encryptionProperties) {
       this.bcryptEncoder = bcryptEncoder;
       this.encryptionProperties = encryptionProperties;
   }

   public String hashPassword(String password) throws Exception {
      String withPepper = addPepper(password);
      return bcryptEncoder.encode(withPepper);
   }

   public boolean CheckPassword(String password, String hashed) throws Exception {
      String withPepper = addPepper(password);
      return bcryptEncoder.matches(withPepper, hashed);
   }

   private String addPepper(String password) throws NoSuchAlgorithmException, InvalidKeyException {
      String pepper = encryptionProperties.getPepper();

      Mac hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKey = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      hmac.init(secretKey);

      byte[] rawHmac = hmac.doFinal(password.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);
   }
}