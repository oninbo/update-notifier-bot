package ru.tinkoff.edu.java.bot.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
public record LinkUpdate(
        Long id,
        @NotBlank URI url,
        String description,
        List<Long> tgChatIds
) {
}
