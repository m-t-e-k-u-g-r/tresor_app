package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * NewSecret
 * @author Peter Rutschmann
 */
@Getter
public class CreateSecretRequest<T extends SecretDto> extends LoginUser {
   @NotNull
   private T secret;
}