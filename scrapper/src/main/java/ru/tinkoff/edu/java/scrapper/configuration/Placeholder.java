package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import ru.tinkoff.edu.java.scrapper.dto.LinkResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListLinksResponse;

@Validated
@ConfigurationProperties(prefix = "placeholder", ignoreUnknownFields = false)
public record Placeholder(
        @NotNull LinkResponse linkResponse,
        @NotNull ListLinksResponse listLinksResponse
) {
}
