package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements BotCommandHandler{
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;
    private final ScrapperClient scrapperClient;

    @Override
    public void handle(BotCommandArguments arguments) {
        var message = applicationConfig.command().start().message().userRegistered();
        scrapperClient.addTgChat(arguments.userId());
        userResponseService.sendMessage(arguments.userId(), message);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof BotCommand.START;
    }
}
