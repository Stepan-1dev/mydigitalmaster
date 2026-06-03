package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskAnalyzeResponse(
        String title,
        String deadline,  // Прилетит строка "yyyy-MM-ddTHH:mm:ss" или null
        String priority,  // LOW, MEDIUM, HIGH, URGENT
        String category   // WORK, STUDY, PERSONAL, HEALTH
) {}