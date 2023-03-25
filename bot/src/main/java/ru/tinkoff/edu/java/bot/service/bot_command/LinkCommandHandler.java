package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

import java.net.URISyntaxException;

@RequiredArgsConstructor
public abstract class LinkCommandHandler implements BotCommandHandler {
    protected final UserResponseService userResponseService;
    protected final ApplicationConfig applicationConfig;
    protected final ScrapperClient scrapperClient;

    @Override
    public void handle(BotCommandArguments arguments) {
        String link = arguments.text().trim();
        if (link.isBlank()) {
            userResponseService.sendMessage(arguments.userId(), noLinkMessage());
        } else {
            try {
                sendLinkToScrapper(link, arguments.userId());
                sendSuccessMessage(arguments.userId());
            } catch (URISyntaxException e) {
                userResponseService.sendMessage(
                        arguments.userId(),
                        applicationConfig.command().common().message().invalidLink()
                );
            }
        }
    }

    protected abstract void sendLinkToScrapper(String link, Long userId) throws URISyntaxException;

    protected abstract void sendSuccessMessage(Long userId);

    protected abstract String noLinkMessage();
}
