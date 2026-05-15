package org.example.service;

import org.example.entity.TokensEntity;
import org.example.repository.TokensRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
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
        //Удаялем предыдущую сессию(На всякий случай)
        repository.deleteByUserId(userId);

        var entityToSave = new TokensEntity(
                null,
                userId,
                hashedRefreshToken,
                Instant.now()
         );

        repository.save(entityToSave);
    }

    @Transactional
    public void deleteByRefreshToken(String refreshToken){
        String hashedRefreshToken = hashToken(refreshToken);
        int deletedCount = repository.deleteByHashedRefreshToken(hashedRefreshToken);
        log.info("deletedCount = " + deletedCount);
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
