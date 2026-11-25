package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterUser
 * @author Peter Rutschmann
 */
public record RegisterUser(
        @NotEmpty(message="Firstname is required.")
        @Size(min=2, max=25, message="Firstname size has to be 2 up to 25 characters.")
        String firstName,

        @NotEmpty (message="Lastname is required.")
        @Size(min=2, max=25, message="Lastname size has to be 2 up to 25 characters.")
        String lastName,

        @NotEmpty (message="E-Mail is required.")
        String email,

        @NotEmpty (message="Password is required.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^0-9A-Za-z]).{8,32}$")
        String password,

        @NotEmpty (message="Password confirmation is required.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^0-9A-Za-z]).{8,32}$")
        String passwordConfirmation,

        @NotEmpty
        String recaptchaToken
) {}
