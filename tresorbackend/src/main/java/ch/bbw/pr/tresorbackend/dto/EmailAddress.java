package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

/**
 * EmailAdress
 * @author Peter Rutschmann
 */
@Getter
public class EmailAddress {
   @NotEmpty (message="E-Mail is required.")
   private String email;
}