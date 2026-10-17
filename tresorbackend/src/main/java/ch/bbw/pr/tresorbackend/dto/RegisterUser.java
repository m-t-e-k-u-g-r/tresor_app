package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * RegisterUser
 * @author Peter Rutschmann
 */
@Getter
public class RegisterUser {

   @NotEmpty(message="Firstname is required.")
   @Size(min=2, max=25, message="Firstname size has to be 2 up to 25 characters.")
   private String firstName;

   @NotEmpty (message="Lastname is required.")
   @Size(min=2, max=25, message="Lastname size has to be 2 up to 25 characters.")
   private String lastName;

   @NotEmpty (message="E-Mail is required.")
   private String email;

   @NotEmpty (message="Password is required.")
   private String password;

   private String recaptchaToken;
}