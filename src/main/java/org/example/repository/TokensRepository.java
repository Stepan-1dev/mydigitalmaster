package org.example.repository;
import org.example.entity.TokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokensRepository extends JpaRepository<TokensEntity, Long> {
    Optional<TokensEntity> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
