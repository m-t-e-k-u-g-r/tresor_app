package ch.bbw.pr.tresorbackend.repository;

import ch.bbw.pr.tresorbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository
 * @author Peter Rutschmann
 */
public interface UserRepository extends JpaRepository<User, Integer> {
   Optional<User> findByEmail(String email);
}
