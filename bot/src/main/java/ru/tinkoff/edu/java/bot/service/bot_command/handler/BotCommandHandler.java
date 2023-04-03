package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;

public interface BotCommandHandler {
    void handle(BotCommandArguments arguments);

    boolean canHandle(BotCommand botCommand);
}
