package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.List;
import java.util.Optional;

public abstract class BotCommand {
    @Component
    public static class START extends BotCommand {
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().start().description();
        }
    }
    @Component
    public static class HELP extends BotCommand{
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().help().description();
        }
    }
    @Component
    public static class TRACK extends BotCommand {

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
    }
    @Component
    @SuppressWarnings("SpellCheckingInspection")
    public static class UNTRACK extends BotCommand {
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
    }
    @Component
    public static class LIST extends BotCommand {
        @Override
        public String getDescription(ApplicationConfig applicationConfig) {
            return applicationConfig.command().list().description();
        }
    }

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

    public static com.pengrad.telegrambot.model.BotCommand[] getTgCommands(
            ApplicationConfig applicationConfig,
            List<BotCommand> botCommands) {
        return botCommands
                .stream()
                .map(c -> c.toTgCommand(applicationConfig))
                .toArray(com.pengrad.telegrambot.model.BotCommand[]::new);
    }
}
