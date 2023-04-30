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
        @NotNull ApplicationConfig.DatabaseAccessType databaseAccessType,
        @Valid @NotNull Database database,
        @NotNull @Valid ApplicationConfig.RabbitMQ rabbitMQ,
        @NotNull Boolean useQueue
) {
    enum DatabaseAccessType {
        JDBC,
        JPA,
        JOOQ
    }
    @Validated
    public record RabbitMQ(@NotBlank String queueName, @NotBlank String exchangeName) {
        private static final String DEAD_LETTER_QUEUE_SUFFIX = ".dlq";

        public String deadLetterQueueName() {
            return queueName.concat(DEAD_LETTER_QUEUE_SUFFIX);
        }

        public String deadLetterExchangeName() {
            return exchangeName.concat(DEAD_LETTER_QUEUE_SUFFIX);
        }
    }
}
