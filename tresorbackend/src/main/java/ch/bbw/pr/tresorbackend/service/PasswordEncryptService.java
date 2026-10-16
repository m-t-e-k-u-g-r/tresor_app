package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.config.EncryptionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequiredArgsConstructor
public class PasswordEncryptService implements PasswordEncoder {
   private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder(12);
   private final EncryptionProperties encryptionProperties;

   @Override
   public String encode(CharSequence rawPassword) {
      String withPepper = addPepper(rawPassword);
      return bcryptEncoder.encode(withPepper);
   }

   @Override
   public boolean matches(CharSequence rawPassword, String encodedPassword) {
      String withPepper = addPepper(rawPassword);
      return bcryptEncoder.matches(withPepper, encodedPassword);
   }

   private String addPepper(CharSequence password) {
      String pepper = encryptionProperties.getPepper();

      try {
         Mac hmac = Mac.getInstance("HmacSHA256");
         SecretKeySpec secretKey = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
         hmac.init(secretKey);

         byte[] pwBytes = password.toString().getBytes(StandardCharsets.UTF_8);
         byte[] rawHmac = hmac.doFinal(pwBytes);
         return Base64.getEncoder().encodeToString(rawHmac);
      } catch (NoSuchAlgorithmException | InvalidKeyException e) {
         throw new RuntimeException("Error adding pepper: " + e.getMessage());
      }
   }
}