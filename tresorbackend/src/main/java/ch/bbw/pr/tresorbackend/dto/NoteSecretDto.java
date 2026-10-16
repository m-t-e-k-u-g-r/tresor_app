package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class NoteSecretDto extends SecretDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;

    @Override
    public SecretType getType() {
        return SecretType.NOTE;
    }

    @Override
    protected NoteSecretDto copy() {
        return NoteSecretDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(this.getContent())
                .build();
    }
}
