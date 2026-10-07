package ch.bbw.pr.tresorbackend.repository;

import ch.bbw.pr.tresorbackend.entities.NoteSecret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteSecretRepository extends JpaRepository<NoteSecret, Long> {
    List<NoteSecret> findAllByUser_Id(Integer userId);
}
