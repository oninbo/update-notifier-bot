package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandHandler;

import java.util.Arrays;
import java.util.List;

@Service
public class BotCommandService {
    private final Logger logger;

    private final List<BotCommandHandler> botCommandHandlers;
    private final UserResponseService userResponseService;

    @Value("${command.common.message.unsupported_command}")
    private String commandNotSupportedMessage;

    public BotCommandService(
            List<BotCommandHandler> botCommandHandlers,
            UserResponseService userResponseService
    ) {
        this.userResponseService = userResponseService;
        this.botCommandHandlers = botCommandHandlers;
        logger = LoggerFactory.getLogger(BotCommandService.class);
    }

    public void handleCommandEntity(Message message, MessageEntity messageEntity) {
        String command = message.text().substring(
                messageEntity.offset(),
                messageEntity.offset() + messageEntity.length()
        );
        String text = message.text().replace(command, "");
        logger.info("command=" + command + ", text=" + text);

        var arguments = new BotCommandArguments(text, message.from().id());
        String commandString = command.substring(1).toUpperCase();
        Arrays.stream(BotCommand.values())
                .filter(v -> v.toString().equals(commandString))
                .findFirst()
                .ifPresentOrElse(
                        botCommand -> handleCommand(botCommand, arguments),
                        () -> {
                            userResponseService.sendMessage(arguments.userId(), commandNotSupportedMessage);
                            handleCommand(BotCommand.HELP, arguments);
                        });
    }

    private void handleCommand(BotCommand botCommand, BotCommandArguments arguments) {
        botCommandHandlers.stream()
                .filter(botCommandHandler -> botCommandHandler.canHandle(botCommand))
                .forEach(botCommandHandler -> botCommandHandler.handle(arguments));
    }
}
