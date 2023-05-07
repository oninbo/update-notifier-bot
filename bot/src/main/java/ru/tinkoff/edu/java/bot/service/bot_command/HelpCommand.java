package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

@Component
public final class HelpCommand extends BotCommand {
    @Override
    public String getDescription(ApplicationConfig applicationConfig) {
        return applicationConfig.command().help().description();
    }

    @Override
    public String getCommandName() {
        return "help";
    }
}
