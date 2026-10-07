package ch.bbw.pr.tresorbackend.mappers;

import ch.bbw.pr.tresorbackend.dto.CreditCardSecretDto;
import ch.bbw.pr.tresorbackend.entities.CreditcardSecret;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardSecretMapper {
    CreditcardSecret toEntity(CreditCardSecretDto creditCardSecretDto);
    CreditCardSecretDto toDto(CreditcardSecret creditcardSecret);
}
