package org.example.entity;


public record AuthInfoForExchange(
    String code,
    String codeVerifier,
    String deviceId,
    String state
) {}
