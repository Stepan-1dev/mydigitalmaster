package org.example.dto;

public record VkAuthResponse(
        String accessToken,
        Long userId
) {}
