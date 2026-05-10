package org.example.entity;

public record UserProfile(
        Long userVkId,

        String firstName,

        String lastName,

        String avatar,

        String sex
) {}
