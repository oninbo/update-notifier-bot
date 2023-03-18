package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record WebClient(
        @Validated @NotNull WebClientSettings github,
        @Validated @NotNull WebClientSettings stackExchange
) {
}
