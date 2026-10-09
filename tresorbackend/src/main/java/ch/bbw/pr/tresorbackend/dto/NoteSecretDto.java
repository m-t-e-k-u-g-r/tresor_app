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
public class NoteSecretDto extends SecretDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;

    @Override
    protected NoteSecretDto copy() {
        return new NoteSecretDto(
                this.getTitle(),
                this.getContent()
        );
    }
}
