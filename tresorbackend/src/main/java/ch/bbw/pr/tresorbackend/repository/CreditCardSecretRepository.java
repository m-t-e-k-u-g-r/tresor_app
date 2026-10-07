package ch.bbw.pr.tresorbackend.repository;

import ch.bbw.pr.tresorbackend.entities.CreditcardSecret;
import ch.bbw.pr.tresorbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardSecretRepository extends JpaRepository<CreditcardSecret, Long> {
    List<CreditcardSecret> findAllByUser_Id(Integer userId);

    Integer user(User user);
}
