package ch.bbw.pr.tresorbackend.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * EncryptUtil
 * Used to encrypt content.
 * Not implemented yet.
 * @author Peter Rutschmann
 */
public class EncryptUtil {
   private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
   private final SecretKey secretKey;

   public EncryptUtil(SecretKey secretKey) {
      this.secretKey = secretKey;
   }

   public String encrypt(String data) throws RuntimeException {
      try {
         Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
         cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);

         byte[] iv = cipher.getIV();
         byte[] encrypted = cipher.doFinal(data.getBytes());

         byte[] combined = new byte[iv.length + encrypted.length];
         System.arraycopy(iv, 0, combined, 0, iv.length);
         System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

         return Base64.getEncoder().encodeToString(combined);
      } catch (Exception e) {
         throw new RuntimeException("Failed to encrypt data: " + e.getMessage());
      }
   }

   public String decrypt(String data) throws RuntimeException {
      try {
         byte[] combined = Base64.getDecoder().decode(data);
         if (combined.length < 12) {
            throw new RuntimeException("Invalid encrypted data");
         }

         byte[] iv = Arrays.copyOfRange(combined, 0, 12);
         byte[] encrypted = Arrays.copyOfRange(combined, 12, combined.length);

         Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
         GCMParameterSpec spec = new GCMParameterSpec(128, iv);
         cipher.init(Cipher.DECRYPT_MODE, this.secretKey, spec);

         byte[] original = cipher.doFinal(encrypted);
         return new String(original, StandardCharsets.UTF_8);
      } catch (Exception e) {
         throw new RuntimeException("Failed to decrypt data: " + e.getMessage());
      }
   }
}
