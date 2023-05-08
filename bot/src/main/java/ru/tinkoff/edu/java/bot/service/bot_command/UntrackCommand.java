package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.List;
import java.util.Optional;

@Component
@SuppressWarnings("SpellCheckingInspection")
public final class UntrackCommand extends BotCommand {
    @Override
    public String getDescription(ApplicationConfig applicationConfig) {
        return applicationConfig.command().untrack().description();
    }

    @Override
    public Optional<String> getMessageInput(ApplicationConfig applicationConfig) {
        return Optional.of(applicationConfig.command().untrack().message().input());
    }

    @Override
    public List<String> getArguments() {
        return List.of("link");
    }

    @Override
    public String getCommandName() {
        return "untrack";
    }
}
