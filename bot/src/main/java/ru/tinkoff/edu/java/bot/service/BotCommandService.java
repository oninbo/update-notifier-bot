package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandHandler;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotCommandService {
    private final Logger logger = LoggerFactory.getLogger(BotCommandService.class);
    private final List<BotCommandHandler> botCommandHandlers;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;

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
                            var messageText = applicationConfig.command().common().message().unsupportedCommand();
                            userResponseService.sendMessage(arguments.userId(), messageText);
                            handleCommand(BotCommand.HELP, arguments);
                        });
    }

    private void handleCommand(BotCommand botCommand, BotCommandArguments arguments) {
        botCommandHandlers.stream()
                .filter(botCommandHandler -> botCommandHandler.canHandle(botCommand))
                .forEach(botCommandHandler -> botCommandHandler.handle(arguments));
    }
}
