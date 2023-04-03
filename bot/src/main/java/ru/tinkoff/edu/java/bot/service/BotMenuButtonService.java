package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.handler.BotCommandHandler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotMenuButtonService {
    private final ApplicationConfig applicationConfig;
    private final List<BotCommandHandler> botCommandHandlers;
    private final UserResponseService userResponseService;
    private final List<BotCommand> botCommands;

    public void handleMessage(Message message) {
        var replyMessage = Optional.ofNullable(message.replyToMessage());
        var isReplyToBot = replyMessage
                .map(userResponseService::isMessageFromBot)
                .orElse(false);
        if (isReplyToBot) {
            handleReply(message);
            return;
        }

        botCommands
                .stream()
                .filter(v -> v.getDescription(applicationConfig).equals(message.text()))
                .findFirst()
                .ifPresent(
                        botCommand -> {
                            var arguments = new BotCommandArguments(null, message.from().id());
                            handleCommand(botCommand, arguments);
                        }
                );
    }

    private void handleCommand(BotCommand botCommand, BotCommandArguments arguments) {
        // Если передали ввод пользователя или он не нужен для выполнения команды,
        // то выполняем команду, иначе запрашиваем ввод пользователя
        if (Objects.nonNull(arguments.text()) || botCommand.getArguments().isEmpty()) {
            botCommandHandlers.stream()
                    .filter(botCommandHandler -> botCommandHandler.canHandle(botCommand))
                    .forEach(botCommandHandler -> botCommandHandler.handle(arguments));
        } else {
            userResponseService.sendMessageForceReply(
                    arguments.userId(),
                    botCommand.getMessageInput(applicationConfig).orElseThrow()
            );
        }
    }

    private void handleReply(Message message) {
        String text = message.replyToMessage().text();
        Optional<BotCommand> botCommand = botCommands
                .stream()
                .filter(
                        value -> value.getMessageInput(applicationConfig)
                                .map(i -> i.equals(text))
                                .orElse(false)
                )
                .findFirst();

        botCommand.ifPresent(
                command -> {
                    var arguments = new BotCommandArguments(
                            message.text(),
                            message.from().id()
                    );
                    handleCommand(command, arguments);
                }
        );
    }
}
