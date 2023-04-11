package ru.tinkoff.edu.java.scrapper.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StackOverflowQuestion(UUID id, Long questionId, OffsetDateTime updatedAt, OffsetDateTime createdAt) {
}
