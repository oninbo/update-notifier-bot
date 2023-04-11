package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record ErrorDescription(
        @NotBlank String api,
        @NotBlank String server,
        @NotBlank String tgChatNotFound,
        @NotBlank String tgChatExists,
        @NotBlank String githubRepositoryNotFound,
        @NotBlank String stackoverflowQuestionNotFound,
        @NotBlank String linkNotSupported,
        @NotBlank String linkExists,
        @NotBlank String linkNotFound
) {
}
