package ru.tinkoff.edu.java.bot.service.bot_command;

public interface BotCommandHandler {
    void handle(BotCommandArguments arguments);

    boolean canHandle(BotCommand botCommand);
}
