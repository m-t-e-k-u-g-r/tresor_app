package ch.bbw.pr.tresorbackend.service.impl;

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

    public void createSecret(NoteSecretDto secret) {
        NoteSecret noteSecret = noteSecretMapper.toEntity(secret);
        noteSecretRepository.save(noteSecret);
    }

    public NoteSecretDto getSecretByIdAndUserId(Long secretId, Integer userId) {
        NoteSecret secret = noteSecretRepository.findByIdAndUser_Id(secretId, userId);
        return secret != null ? noteSecretMapper.toDto(secret) : null;
    }

    public NoteSecretDto updateSecretByIdAndUserId(Long secretId, Integer userId, NoteSecretDto secret) {
        NoteSecret noteSecret = SafeDbCall.safeDbCall(() -> noteSecretRepository.findByIdAndUser_Id(secretId, userId), null);
        if (noteSecret == null) {
            return null;
        }
        noteSecret.setTitle(secret.getTitle());
        noteSecret.setContent(secret.getContent());
        noteSecretRepository.save(noteSecret);
        return noteSecretMapper.toDto(noteSecret);
    }

    public void deleteSecretByIdAndUserId(Long secretId, Integer userId) {
        SafeDbCall.safeDbCall(() -> {
            NoteSecret secret = noteSecretRepository.findByIdAndUser_Id(secretId, userId);
            if (secret != null) {
                noteSecretRepository.delete(secret);
            }
        });
    }
}
