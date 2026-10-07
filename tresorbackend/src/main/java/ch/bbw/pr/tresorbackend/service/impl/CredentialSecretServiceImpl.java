package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.dto.CreateSecretRequest;
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

    public void createSecret(CreateSecretRequest<CredentialSecretDto> secret) {
        CredentialSecret credentialSecret = credentialSecretMapper.toEntity(secret.getSecret());
        credentialSecretRepository.save(credentialSecret);
    }
    
    public CredentialSecretDto getSecretById(Long secretId) {
        CredentialSecret secret = credentialSecretRepository.findById(secretId)
                .orElseThrow();
        return credentialSecretMapper.toDto(secret);
    }

    public CredentialSecretDto updateSecret(Long secretId, CredentialSecretDto secret) {
        CredentialSecret credentialSecret = credentialSecretRepository.findById(secretId)
                .orElseThrow();
        credentialSecret.setUsername(secret.getUsername());
        credentialSecret.setPassword(secret.getPassword());
        credentialSecret.setUrl(secret.getUrl());
        credentialSecretRepository.save(credentialSecret);
        return credentialSecretMapper.toDto(credentialSecret);
    }

    public void deleteSecret(Long secretId) {
        SafeDbCall.safeDbCall(() -> credentialSecretRepository.deleteById(secretId));
    }
}
