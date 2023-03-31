package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

@Component
public non-sealed class ListCommand extends BotCommand {
    @Override
    public String getDescription(ApplicationConfig applicationConfig) {
        return applicationConfig.command().list().description();
    }

    @Override
    public String getCommandName() {
        return "list";
    }
}
