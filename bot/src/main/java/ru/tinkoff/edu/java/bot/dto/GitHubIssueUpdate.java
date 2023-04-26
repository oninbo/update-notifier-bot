package ru.tinkoff.edu.java.bot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
public record GitHubIssueUpdate(
        @NotNull URI issueUrl,
        @NotNull @Valid GitHubRepository repository,
        @NotEmpty List<@NotNull Long> chatIds) {
    @Validated
    public record GitHubRepository(@NotNull URI url, @NotBlank String name, @NotBlank String username){
    }
}
