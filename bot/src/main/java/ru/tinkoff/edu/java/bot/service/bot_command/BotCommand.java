package ru.tinkoff.edu.java.bot.service.bot_command;

import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.Arrays;
import java.util.Optional;

public enum BotCommand {
    START {
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().start().description();
        }
    },
    HELP {
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().help().description();
        }
    },
    TRACK {

        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().track().description();
        }

        @Override
        public String getMessageInput(ApplicationConfig applicationConfig) {
            return applicationConfig.command().track().message().input();
        }

        @Override
        public Optional<String[]> getArguments() {
            return Optional.of(new String[]{"link"});
        }
    },
    @SuppressWarnings("SpellCheckingInspection")
    UNTRACK {
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().untrack().description();
        }

        @Override
        public String getMessageInput(ApplicationConfig applicationConfig) {
            return applicationConfig.command().untrack().message().input();
        }

        @Override
        public Optional<String[]> getArguments() {
            return Optional.of(new String[]{"link"});
        }
    },
    LIST {
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().list().description();
        }
    }; // показать список отслеживаемых ссылок

    public abstract String getDescription(ApplicationConfig applicationConfig);

    public String getMessageInput(ApplicationConfig applicationConfig) {
        return null;
    }

    public Optional<String[]> getArguments() {
        return Optional.empty();
    }

    public com.pengrad.telegrambot.model.BotCommand toTgCommand(ApplicationConfig applicationConfig) {
        return new com.pengrad.telegrambot.model.BotCommand(
                toString().toLowerCase(),
                getDescription(applicationConfig)
        );
    }

    public static com.pengrad.telegrambot.model.BotCommand[] getTgCommands(ApplicationConfig applicationConfig) {
        return Arrays.stream(values())
                .map(c -> c.toTgCommand(applicationConfig))
                .toArray(com.pengrad.telegrambot.model.BotCommand[]::new);
    }
}
