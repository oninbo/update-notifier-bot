package ru.tinkoff.edu.java.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandHandler;

import java.util.List;

@Service
public class BotCommandService {
    private final Logger logger;

    private final List<BotCommandHandler> botCommandHandlers;

    public BotCommandService(List<BotCommandHandler> botCommandHandlers) {
        this.botCommandHandlers = botCommandHandlers;
        logger = LoggerFactory.getLogger(BotCommandService.class);
    }

    public void handleCommand(String command, String arguments, UserResponseService.Sender userResponseSender) {
        logger.info("command=" + command + ", arguments=" + arguments);
        BotCommand botCommand;
        try {
            botCommand = BotCommand.valueOf(command.substring(1).toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BotCommandNotSupportedException();
        }
        botCommandHandlers.stream()
                .filter(botCommandHandler -> botCommandHandler.canHandle(botCommand))
                .forEach(botCommandHandler -> botCommandHandler.handle(arguments, userResponseSender));
    }
}
