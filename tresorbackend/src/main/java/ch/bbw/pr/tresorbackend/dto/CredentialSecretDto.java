package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialSecretDto extends SecretDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String url;

    @Override
    protected CredentialSecretDto copy() {
        return new CredentialSecretDto(
                this.getUsername(),
                this.getPassword(),
                this.getUrl()
        );
    }
}
