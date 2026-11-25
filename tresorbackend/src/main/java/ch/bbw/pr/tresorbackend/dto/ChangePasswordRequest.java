package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ChangePasswordRequest
 *
 * @author Matteo Krüger
 */
public record ChangePasswordRequest(
        @NotBlank @Email String email,
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {}
