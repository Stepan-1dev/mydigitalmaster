package org.example.repository;
import org.example.entity.TokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokensRepository extends JpaRepository<TokensEntity, Long> {

    @Modifying // Сообщаем Spring, что это не просто SELECT, а изменение данных
    @Transactional
    void deleteByHashedRefreshTokenAndUserId(String hashedRefreshToken, long userId);

    @Modifying // Сообщаем Spring, что это не просто SELECT, а изменение данных
    @Transactional // Разрешаем выполнение транзакции на уровне метода
    void deleteByUserId(Long userId);

    @Transactional
    Optional<TokensEntity> findByHashedRefreshToken(String hashedRefreshToken);

    @Modifying
    @Transactional
    void deleteByHashedRefreshToken(String hashedRefreshToken);
}