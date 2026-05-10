package org.example.dto;

import org.example.entity.UserProfile;

public record AuthResponse(
        UserProfile userProfile,
        String accessToken,
        String refreshToken
) { }
