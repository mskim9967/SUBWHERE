package cau.capstone.ottitor.repository;

import cau.capstone.ottitor.domain.Jwt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {

    Optional<Jwt> findOneByUserId(Long userId);

    void deleteByUserId(Long userId);
}
