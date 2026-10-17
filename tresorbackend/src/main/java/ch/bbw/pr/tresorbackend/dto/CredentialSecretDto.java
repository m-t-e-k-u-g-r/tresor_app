package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CredentialSecretDto extends SecretDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String url;

    @Override
    public SecretType getType() {
        return SecretType.CREDENTIAL;
    }

    @Override
    protected CredentialSecretDto copy() {
        return CredentialSecretDto.builder()
                .id(this.getId())
                .username(this.getUsername())
                .password(this.getPassword())
                .url(this.getUrl())
                .build();
    }
}
