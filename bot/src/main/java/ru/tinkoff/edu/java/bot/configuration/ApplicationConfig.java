package ru.tinkoff.edu.java.bot.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @Valid @NotNull ErrorDescription errorDescription,
        @Valid BotConfig botConfig,
        @Valid @NotNull Command command,
        @Valid @NotNull WebClient webClient
) {
}

