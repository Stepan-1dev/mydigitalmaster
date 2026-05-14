package org.example.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Table(name = "tokens")
@Entity
public class TokensEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token", unique = true, nullable = false)
    private String refreshToken;

    @Column(name = "user_id", nullable = false)
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
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    public TokensEntity(Long id, long userId, String refreshToken, Instant expiryDate) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }
}
