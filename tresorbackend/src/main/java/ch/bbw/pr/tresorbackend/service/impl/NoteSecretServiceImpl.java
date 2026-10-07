package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.dto.CreateSecretRequest;
import ch.bbw.pr.tresorbackend.dto.NoteSecretDto;
import ch.bbw.pr.tresorbackend.entities.NoteSecret;
import ch.bbw.pr.tresorbackend.mappers.NoteSecretMapper;
import ch.bbw.pr.tresorbackend.repository.NoteSecretRepository;
import ch.bbw.pr.tresorbackend.service.SafeDbCall;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NoteSecretServiceImpl {
    private final NoteSecretRepository noteSecretRepository;
    private final NoteSecretMapper noteSecretMapper;

    public List<NoteSecretDto> getAllSecrets() {
        return SafeDbCall.safeDbCall(() -> noteSecretRepository
                .findAll()
                .stream().map(noteSecretMapper::toDto)
                .toList(), null);
    }

    public List<NoteSecretDto> getSecretsByUserId(Integer userId) {
        return SafeDbCall.safeDbCall(() -> noteSecretRepository
                .findAllByUser_Id(userId)
                .stream().map(noteSecretMapper::toDto)
                .toList(), null);
    }

    public void createSecret(CreateSecretRequest<NoteSecretDto> secret) {
        NoteSecret noteSecret = noteSecretMapper.toEntity(secret.getSecret());
        noteSecretRepository.save(noteSecret);
    }

    public NoteSecretDto getSecretById(Long secretId) {
        NoteSecret secret = noteSecretRepository.findById(secretId)
                .orElseThrow();
        return noteSecretMapper.toDto(secret);
    }

    public NoteSecretDto updateSecret(Long secretId, NoteSecretDto secret) {
        NoteSecret noteSecret = SafeDbCall.safeDbCall(() -> noteSecretRepository.findById(secretId)
                .orElseThrow(), null);
        assert noteSecret != null;
        noteSecret.setTitle(secret.getTitle());
        noteSecret.setContent(secret.getContent());
        noteSecretRepository.save(noteSecret);
        return noteSecretMapper.toDto(noteSecret);
    }

    public void deleteSecret(Long secretId) {
        SafeDbCall.safeDbCall(() -> noteSecretRepository.deleteById(secretId));
    }
}
