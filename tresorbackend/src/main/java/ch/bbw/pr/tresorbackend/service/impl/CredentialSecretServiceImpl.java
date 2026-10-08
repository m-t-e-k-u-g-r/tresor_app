package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.dto.CredentialSecretDto;
import ch.bbw.pr.tresorbackend.entities.CredentialSecret;
import ch.bbw.pr.tresorbackend.mappers.CredentialSecretMapper;
import ch.bbw.pr.tresorbackend.repository.CredentialSecretRepository;
import ch.bbw.pr.tresorbackend.service.SafeDbCall;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CredentialSecretServiceImpl {
    private final CredentialSecretRepository credentialSecretRepository;
    private final CredentialSecretMapper credentialSecretMapper;

    public List<CredentialSecretDto> getAllSecrets() {
        return SafeDbCall.safeDbCall(() -> credentialSecretRepository
                .findAll()
                .stream().map(credentialSecretMapper::toDto)
                .toList(), null);
    }

    public List<CredentialSecretDto> getSecretsByUserId(Integer userId) {
        return SafeDbCall.safeDbCall(() -> credentialSecretRepository
                .findAllByUser_Id(userId)
                .stream().map(credentialSecretMapper::toDto)
                .toList(), null);
    }

    public void createSecret(CredentialSecretDto secret) {
        CredentialSecret credentialSecret = credentialSecretMapper.toEntity(secret);
        credentialSecretRepository.save(credentialSecret);
    }
    
    public CredentialSecretDto getSecretByIdAndUserId(Long secretId, Integer userId) {
        CredentialSecret secret = SafeDbCall.safeDbCall(() -> credentialSecretRepository.findByIdAndUser_Id(secretId, userId), null);
        return secret != null ? credentialSecretMapper.toDto(secret) : null;
    }

    public CredentialSecretDto updateSecretByIdAndUserId(Long secretId, Integer userId, CredentialSecretDto secret) {
        CredentialSecret credentialSecret = SafeDbCall.safeDbCall(() -> credentialSecretRepository.findByIdAndUser_Id(secretId, userId), null);
        if (credentialSecret == null) {
            return null;
        }
        credentialSecret.setUsername(secret.getUsername());
        credentialSecret.setPassword(secret.getPassword());
        credentialSecret.setUrl(secret.getUrl());
        SafeDbCall.safeDbCall(() -> credentialSecretRepository.save(credentialSecret));
        return credentialSecretMapper.toDto(credentialSecret);
    }

    public void deleteSecretByIdAndUserId(Long secretId, Integer userId) {
        SafeDbCall.safeDbCall(() -> {
            CredentialSecret secret = credentialSecretRepository.findByIdAndUser_Id(secretId, userId);
            if (secret != null) {
                credentialSecretRepository.delete(secret);
            }
        });
    }
}
