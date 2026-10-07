package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * NewSecret
 * @author Peter Rutschmann
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSecretRequest<T extends SecretDto> {
   @NotEmpty (message="email is required.")
   private String email;

   @NotEmpty (message="encryption password id is required.")
   private String encryptPassword;

   @NotNull
   private T secret;
}