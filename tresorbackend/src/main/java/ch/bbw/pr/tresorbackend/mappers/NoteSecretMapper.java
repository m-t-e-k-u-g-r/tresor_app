package ch.bbw.pr.tresorbackend.mappers;

import ch.bbw.pr.tresorbackend.dto.NoteSecretDto;
import ch.bbw.pr.tresorbackend.entities.NoteSecret;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteSecretMapper {
    NoteSecret toEntity(NoteSecretDto noteSecretDto);
    NoteSecretDto toDto(NoteSecret noteSecret);
}
