package ru.tinkoff.edu.java.bot.service.bot_command;

import ru.tinkoff.edu.java.bot.service.UserResponseService;

public interface BotCommandHandler {
    void handle(String arguments, UserResponseService.Sender userResponseSender);

    boolean canHandle(BotCommand botCommand);
}
