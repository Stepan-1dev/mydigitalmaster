package org.example.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Table(name = "tokens")
@Entity
public class TokensEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token_hash", unique = true, nullable = false)
    private String hashedRefreshToken;

    @Column(name = "user_id", nullable = false, unique = true)
    private long userId;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    public TokensEntity() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getHashedRefreshToken() {
        return hashedRefreshToken;
    }

    public void setHashedRefreshToken(String hashedRefreshToken) {
        this.hashedRefreshToken = hashedRefreshToken;
    }

    public TokensEntity(Long id, long userId, String refreshTokenHash, Instant expiryDate) {
        this.id = id;
        this.hashedRefreshToken = refreshTokenHash;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }
}
