package org.burgerapp.repository;

import org.burgerapp.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByAuthId(Long authId);
}