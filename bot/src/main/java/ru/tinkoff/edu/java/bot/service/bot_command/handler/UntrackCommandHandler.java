package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.RemoveLinkRequest;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.UntrackCommand;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("SpellCheckingInspection")
@Component
public class UntrackCommandHandler extends LinkCommandHandler {
    public UntrackCommandHandler(
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig,
            ScrapperClient scrapperClient,
            LinkParserService linkParserService
    ) {
        super(userResponseService, applicationConfig, scrapperClient, linkParserService);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof UntrackCommand;
    }

    @Override
    protected void sendLinkToScrapper(String link, Long userId) throws URISyntaxException {
        scrapperClient.deleteLink(userId, new RemoveLinkRequest(new URI(link)));
    }

    @Override
    protected void sendSuccessMessage(Long userId) {
        userResponseService.sendMessage(userId, applicationConfig.command().untrack().message().success());
    }

    @Override
    protected String noLinkMessage() {
        return applicationConfig.command().untrack().message().noLink();
    }
}