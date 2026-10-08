package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.dto.CreditCardSecretDto;
import ch.bbw.pr.tresorbackend.entities.CreditcardSecret;
import ch.bbw.pr.tresorbackend.mappers.CreditCardSecretMapper;
import ch.bbw.pr.tresorbackend.repository.CreditCardSecretRepository;
import ch.bbw.pr.tresorbackend.service.SafeDbCall;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CreditCardSecretServiceImpl {
    private final CreditCardSecretRepository creditCardSecretRepository;
    private final CreditCardSecretMapper creditCardSecretMapper;

    public List<CreditCardSecretDto> getAllSecrets() {
        return SafeDbCall.safeDbCall(() -> creditCardSecretRepository
                .findAll()
                .stream().map(creditCardSecretMapper::toDto)
                .toList(), null);
    }

    public List<CreditCardSecretDto> getSecretsByUserId(Integer userId) {
        return SafeDbCall.safeDbCall(() -> creditCardSecretRepository
                .findAllByUser_Id(userId)
                .stream().map(creditCardSecretMapper::toDto)
                .toList(), null);
    }

    public void createSecret(CreditCardSecretDto secret) {
        CreditcardSecret creditcardSecret = creditCardSecretMapper.toEntity(secret);
        SafeDbCall.safeDbCall(() -> creditCardSecretRepository.save(creditcardSecret));
    }

    public CreditCardSecretDto getSecretByIdAndUserId(Long secretId, Integer userId) {
        CreditcardSecret secret = SafeDbCall.safeDbCall(() -> creditCardSecretRepository.findByIdAndUser_Id(secretId, userId), null);
        return secret != null ? creditCardSecretMapper.toDto(secret) : null;
    }
    
    public CreditCardSecretDto updateSecretByIdAndUserId(Long secretId, Integer userId, CreditCardSecretDto secret) {
        CreditcardSecret creditcardSecret = SafeDbCall.safeDbCall(() -> creditCardSecretRepository.findByIdAndUser_Id(secretId, userId), null);
        if (creditcardSecret == null) {
            return null;
        }
        creditcardSecret.setCardType(secret.getCardType());
        creditcardSecret.setCardNumber(secret.getCardNumber());
        creditcardSecret.setCvv(secret.getCvv());
        creditcardSecret.setExpiration(secret.getExpiration());
        SafeDbCall.safeDbCall(() -> creditCardSecretRepository.save(creditcardSecret));
        return creditCardSecretMapper.toDto(creditcardSecret);
    }

    public void deleteSecretByIdAndUserId(Long secretId, Integer userId) {
        SafeDbCall.safeDbCall(() -> {
            CreditcardSecret secret = creditCardSecretRepository.findByIdAndUser_Id(secretId, userId);
            if (secret != null) {
                creditCardSecretRepository.delete(secret);
            }
        });
    }
}
