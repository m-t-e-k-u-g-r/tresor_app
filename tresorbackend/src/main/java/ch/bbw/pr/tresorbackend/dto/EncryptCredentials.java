package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

/**
 * EncryptCredentials
 * @author Peter Rutschmann
 */
@Getter
public class EncryptCredentials {
   private Integer userId;
   private String email;
   @NotEmpty(message="Password is required.")
   private String password;
}
