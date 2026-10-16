package ch.bbw.pr.tresorbackend.repository;

import ch.bbw.pr.tresorbackend.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByRole(String role);
}
