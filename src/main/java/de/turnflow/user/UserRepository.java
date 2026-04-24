package de.turnflow.user;

import de.turnflow.user.entity.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

    @EntityGraph(attributePaths = {"roles", "member"})
    Optional<UserAccount> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "member"})
    Optional<UserAccount> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "member"})
    Optional<UserAccount> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}