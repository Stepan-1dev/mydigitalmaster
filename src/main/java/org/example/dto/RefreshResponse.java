package org.example.dto;

public record RefreshResponse(
        String accessToken,
        String refreshToken
) {
}
