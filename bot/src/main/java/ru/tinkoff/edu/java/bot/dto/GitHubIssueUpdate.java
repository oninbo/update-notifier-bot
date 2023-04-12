package ru.tinkoff.edu.java.bot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
public record GitHubIssueUpdate(
        @NotNull URI issueUrl,
        @NotNull URI repositoryUrl,
        @NotEmpty List<@NotNull Long> chatIds) {
}
