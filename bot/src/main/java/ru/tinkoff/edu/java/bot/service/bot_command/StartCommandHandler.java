package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements BotCommandHandler{
    private final UserResponseService userResponseService;

    @Value("${command.start.message.user_registered}")
    private String userRegisteredMessage;

    @Override
    public void handle(BotCommandArguments arguments) {
        userResponseService.sendMessage(arguments.userId(), userRegisteredMessage);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand == BotCommand.START;
    }
}
