package org.example.service;

import org.example.entity.TokensEntity;
import org.example.repository.TokensRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokensService {
    private TokensRepository repository;

    public TokensService(TokensRepository repository){
        this.repository = repository;
    }

    public String generateRefreshToken(){
        return UUID.randomUUID().toString();
    }

    public void create(Long userId, String refreshToken){
        var entityToSave = new TokensEntity(
                null,
                userId,
                refreshToken,
                Instant.now()
         );

        repository.save(entityToSave);
    }
}
