package ru.tinkoff.edu.java.bot.service.bot_command;


import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.Optional;

@Component
public non-sealed class TrackCommand extends BotCommand {

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