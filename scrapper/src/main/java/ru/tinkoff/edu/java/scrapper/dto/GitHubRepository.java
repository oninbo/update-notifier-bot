package ru.tinkoff.edu.java.scrapper.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GitHubRepository(
        UUID id,
        String username,
        String name,
        OffsetDateTime updatedAt,
        OffsetDateTime createdAt
) {
}
