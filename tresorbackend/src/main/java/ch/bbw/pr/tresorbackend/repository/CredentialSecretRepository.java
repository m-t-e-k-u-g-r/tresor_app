package ch.bbw.pr.tresorbackend.repository;

import ch.bbw.pr.tresorbackend.entities.CredentialSecret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredentialSecretRepository extends JpaRepository<CredentialSecret, Long> {
    List<CredentialSecret> findAllByUser_Id(Integer userId);
    CredentialSecret findByIdAndUser_Id(Long id, Integer userId);
}
