package ru.tinkoff.edu.java.bot.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record WebClient(
        @Validated @NotNull WebClientConfig scrapper
) {
}
