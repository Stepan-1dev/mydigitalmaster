package org.example.service;

import org.example.entity.TokensEntity;
import org.example.exception.TokenExpiredException;
import org.example.exception.TokenNotFoundException;
import org.example.repository.TokensRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokensService {
    private final Logger log = LoggerFactory.getLogger(TokensService.class);

    private TokensRepository repository;

    public TokensService(TokensRepository repository){
        this.repository = repository;
    }

    public String generateRefreshToken(){
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void create(Long userId, String hashedRefreshToken){
        var entityToSave = new TokensEntity(
                null,
                userId,
                hashedRefreshToken,
                Instant.now().plus(30, ChronoUnit.DAYS)
         );

        repository.save(entityToSave);
    }

    @Transactional
    public void deleteByHashedRefreshTokenAndUserId(String hashedRefreshToken,long userId){
        repository.deleteByHashedRefreshTokenAndUserId(hashedRefreshToken, userId);
    }

    @Transactional
    public void deleteByHashedRefreshToken(String hashedRefreshToken){
        repository.deleteByHashedRefreshToken(hashedRefreshToken);    }

    @Transactional
    public TokensEntity findByHashedRefreshToken(String hashedRefreshToken){
        return repository.findByHashedRefreshToken(hashedRefreshToken)
                .orElseThrow(() -> new TokenNotFoundException("Данная сессия не найдена"));
    }

    public void validateExpiry(TokensEntity tokensEntity){
        if(tokensEntity.getExpiryDate().isBefore(Instant.now())){

            //Удаляем базу от истекшего токена
            repository.deleteByHashedRefreshToken(tokensEntity.getHashedRefreshToken());

            //Выкидываем ошибку, что токен истек
            throw new TokenExpiredException("Данный токен истек");
        }
    }

    public String hashToken(String rawToken) {
        try {
            // Используем стандартный алгоритм SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            // Переводим массив байтов в удобную строку Base64
            return Base64.getEncoder().encodeToString(encodedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хэширования токена", e);
        }
    }
}


