package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @Valid @NotNull ErrorDescription errorDescription,
        @Valid @NotNull WebClient webClient,
        @Valid @NotNull Scheduler scheduler
) {
}
