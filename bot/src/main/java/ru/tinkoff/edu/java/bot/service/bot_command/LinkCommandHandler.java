package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@RequiredArgsConstructor
public abstract class LinkCommandHandler implements BotCommandHandler {
    protected final UserResponseService userResponseService;
    protected final ApplicationConfig applicationConfig;

    @Override
    public void handle(BotCommandArguments arguments) {
        String link = arguments.text().trim();
        if (link.isBlank()) {
            userResponseService.sendMessage(arguments.userId(), noLinkMessage());
        } else {
            sendLinkToScrapper(link, arguments.userId());
            sendSuccessMessage(arguments.userId());
        }
    }

    protected abstract void sendLinkToScrapper(String link, Long userId);

    protected abstract void sendSuccessMessage(Long userId);

    protected abstract String noLinkMessage();
}
