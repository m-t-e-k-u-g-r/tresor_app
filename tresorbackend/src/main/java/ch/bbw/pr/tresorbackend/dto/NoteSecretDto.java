package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteSecretDto extends SecretDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
