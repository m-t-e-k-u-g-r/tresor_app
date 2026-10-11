package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

/**
 * LoginUser
 *   DTO for simple login
 * @author Peter Rutschmann
 */
@Getter
public class LoginUser extends EmailAddress {
    @NotEmpty(message="Password is required.")
    private String password;
}
