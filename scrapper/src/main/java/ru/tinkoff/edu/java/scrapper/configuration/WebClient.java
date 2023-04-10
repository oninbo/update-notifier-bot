package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record WebClient(
        @Validated @NotNull WebClientConfig github,
        @Validated @NotNull WebClientConfig stackExchange,
        @NotBlank String botBaseUrl
) {
}
