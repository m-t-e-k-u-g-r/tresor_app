package ch.bbw.pr.tresorbackend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * PasswordEncryptService
 *   used to hash password and verify match
 * @author Peter Rutschmann
 */
@Service
public class PasswordEncryptService {
   private final BCryptPasswordEncoder bcryptEncoder;

   public PasswordEncryptService(BCryptPasswordEncoder bcryptEncoder) {
       this.bcryptEncoder = bcryptEncoder;
   }

   public String hashPassword(String password) {
      return bcryptEncoder.encode(password);
   }

   public boolean CheckPassword(String password, String hashed) {
      return bcryptEncoder.matches(password, hashed);
   }

}