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

@SuppressWarnings("SpellCheckingInspection")
@Component
public final class UntrackCommandHandler extends LinkCommandHandler {
    public UntrackCommandHandler(
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig,
            ScrapperClient scrapperClient,
            final LinkParserService linkParserService
    ) {
        super(userResponseService, applicationConfig, scrapperClient, linkParserService);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof UntrackCommand;
    }

    @Override
    protected void sendLinkToScrapper(URI link, Long userId) {
        getScrapperClient().deleteLink(userId, new RemoveLinkRequest(link));
    }

    @Override
    protected void sendSuccessMessage(Long userId) {
        getUserResponseService().sendMessage(userId, getApplicationConfig().command().untrack().message().success());
    }

    @Override
    protected String noLinkMessage() {
        return getApplicationConfig().command().untrack().message().noLink();
    }
}
