package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @Valid @NotNull ErrorDescription errorDescription,
        @Valid @NotNull WebClient webClient,
        @Valid @NotNull Scheduler scheduler,
        @Valid @NotNull Database database,
        @NotNull ApplicationConfig.DatabaseAccessType databaseAccessType,
        @NotNull @Valid ApplicationConfig.RabbitMQ rabbitMQ
) {
    enum DatabaseAccessType {
        JDBC,
        JPA,
        JOOQ
    }

    @Validated
    record RabbitMQ(@NotBlank String queueName, @NotBlank String exchangeName) {
    }
}
