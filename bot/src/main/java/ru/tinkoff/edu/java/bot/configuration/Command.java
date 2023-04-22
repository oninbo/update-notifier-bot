package ru.tinkoff.edu.java.bot.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record Command(
        @Valid @NotNull Common common,
        @Valid @NotNull Start start,
        @Valid @NotNull Help help,
        @Valid @NotNull Track track,
        @Valid @NotNull Untrack untrack,
        @Valid @NotNull List list
) {

    @Validated
    public record Common(@Valid @NotNull Message message) {
        @Validated
        public record Message(
                @NotBlank String unsupportedCommand,
                @NotBlank String invalidLink
        ) {
        }
    }

    @Validated
    public record Start(@NotBlank String description, @Valid @NotNull Message message) {
        @Validated
        public record Message(@NotBlank String userRegistered) {
        }
    }

    @Validated
    public record Help(
            @NotBlank String description,
            @NotBlank String header
    ) {

    }

    @Validated
    public record List(
            @NotBlank String description,
            @NotBlank String header,
            @Valid @NotNull Message message
    ) {
        @Validated
        public record Message(@NotBlank String noLinks) {
        }
    }

    @Validated
    public record Track(@NotBlank String description, @Valid @NotNull Message message) {
        @Validated
        public record Message(
                @NotBlank String noLink,
                @NotBlank String input,
                @NotBlank String success
        ) {
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Validated
    public record Untrack(@NotBlank String description, @Valid @NotNull Message message) {
        @Validated
        public record Message(
                @NotBlank String noLink,
                @NotBlank String input,
                @NotBlank String success
        ) {
        }
    }
}
