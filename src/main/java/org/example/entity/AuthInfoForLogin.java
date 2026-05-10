package org.example.entity;


public record AuthInfoForLogin(
    String code,
    String codeVerifier,
    String deviceId,
    String state
) {}
