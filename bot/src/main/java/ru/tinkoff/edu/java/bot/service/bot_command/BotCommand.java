package ru.tinkoff.edu.java.bot.service.bot_command;

import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.Optional;

public abstract sealed class BotCommand permits
        HelpCommand,
        ListCommand,
        StartCommand,
        TrackCommand,
        UntrackCommand {
    public abstract String getDescription(ApplicationConfig applicationConfig);

    public Optional<String> getMessageInput(ApplicationConfig applicationConfig) {
        return Optional.empty();
    }

    public Optional<String[]> getArguments() {
        return Optional.empty();
    }

    public com.pengrad.telegrambot.model.BotCommand toTgCommand(ApplicationConfig applicationConfig) {
        return new com.pengrad.telegrambot.model.BotCommand(
                getCommandName(),
                getDescription(applicationConfig)
        );
    }

    public abstract String getCommandName();

    public static com.pengrad.telegrambot.model.BotCommand[] getTgCommands(
            ApplicationConfig applicationConfig,
            java.util.List<BotCommand> botCommands
    ) {
        return botCommands
                .stream()
                .map(c -> c.toTgCommand(applicationConfig))
                .toArray(com.pengrad.telegrambot.model.BotCommand[]::new);
    }
}
