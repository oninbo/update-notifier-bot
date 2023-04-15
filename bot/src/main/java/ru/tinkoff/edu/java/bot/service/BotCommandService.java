package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.HelpCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.handler.BotCommandHandler;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotCommandService {
    private final List<BotCommandHandler> botCommandHandlers;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;
    private final List<BotCommand> botCommands;
    private final HelpCommand helpCommand;

    public void handleCommandEntity(Message message, MessageEntity messageEntity) {
        String command = message.text().substring(
                messageEntity.offset(),
                messageEntity.offset() + messageEntity.length()
        );
        String text = message.text().replace(command, "");
        log.info("command=" + command + ", text=" + text);

        var arguments = new BotCommandArguments(text, message.from().id());
        String commandString = command.substring(1).toLowerCase();
        botCommands
                .stream()
                .filter(v -> v.getCommandName().equals(commandString))
                .findFirst()
                .ifPresentOrElse(
                        botCommand -> handleCommand(botCommand, arguments),
                        () -> {
                            var messageText = applicationConfig.command().common().message().unsupportedCommand();
                            userResponseService.sendMessage(arguments.userId(), messageText);
                            handleCommand(helpCommand, arguments);
                        });
    }

    private void handleCommand(BotCommand botCommand, BotCommandArguments arguments) {
        botCommandHandlers.stream()
                .filter(botCommandHandler -> botCommandHandler.canHandle(botCommand))
                .forEach(botCommandHandler -> botCommandHandler.handle(arguments));
    }
}
