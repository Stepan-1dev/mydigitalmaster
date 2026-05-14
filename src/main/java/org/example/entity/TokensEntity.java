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
    private String refreshTokenHash;

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

    public String getRefreshToken() {
        return refreshTokenHash;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshTokenHash = refreshToken;
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

    public TokensEntity(Long id, long userId, String refreshTokenHash, Instant expiryDate) {
        this.id = id;
        this.refreshTokenHash = refreshTokenHash;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }
}
