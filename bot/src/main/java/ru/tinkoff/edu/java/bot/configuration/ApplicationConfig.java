package ru.tinkoff.edu.java.bot.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @Valid @NotNull ErrorDescription errorDescription,
        @Valid BotConfig botConfig,
        @Valid @NotNull Command command,
        @Valid @NotNull WebClient webClient,

        @Valid @NotNull Message message,
        @Valid @NotNull RabbitMQ rabbitMQ
) {
    @Validated
    public record Message(
            @NotBlank String update,
            @NotBlank String unsupportedLink,
            @NotBlank String stackoverflowAnswerUpdate,
            @NotBlank String githubIssueUpdate,
            @NotBlank String error
    ) {
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

