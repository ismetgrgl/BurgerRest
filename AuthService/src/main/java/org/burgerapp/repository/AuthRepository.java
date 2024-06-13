package org.burgerapp.repository;

import org.burgerapp.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth , Long> {
    Boolean existsByUsername(String username);
    /**
     * email ve password vt'da kayıtlı mı kontrolü yapar.
     */
    Optional<Auth> findOptionalByEmailAndPassword(String email, String password);

    Optional<Auth> findByEmailAndCode(String email, String passwordResetCode);

    Optional<Auth>  findByEmail(String email);
}
