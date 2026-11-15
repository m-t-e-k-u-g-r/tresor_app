package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ChangePasswordRequest
 *
 * @author Matteo Krüger
 */
public record ChangePasswordRequest(
        @NotBlank String email,
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {}
