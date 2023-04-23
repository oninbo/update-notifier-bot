package ru.tinkoff.edu.java.bot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Validated
public record LinkUpdate(
        UUID id,
        @NotNull URI url,
        String description,
        @NotEmpty List<@NotNull Long> tgChatIds
) {
}
