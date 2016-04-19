package au.gov.dto.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import au.gov.dto.security.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmail(String email);
}
