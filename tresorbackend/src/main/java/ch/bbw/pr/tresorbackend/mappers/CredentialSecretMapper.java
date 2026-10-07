package ch.bbw.pr.tresorbackend.mappers;

import ch.bbw.pr.tresorbackend.dto.CredentialSecretDto;
import ch.bbw.pr.tresorbackend.entities.CredentialSecret;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialSecretMapper {
    CredentialSecret toEntity(CredentialSecretDto credentialSecretDto);
    CredentialSecretDto toDto(CredentialSecret credentialSecret);
}
