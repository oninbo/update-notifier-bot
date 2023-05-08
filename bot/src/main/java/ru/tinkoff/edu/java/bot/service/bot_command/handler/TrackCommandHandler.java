package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.AddLinkRequest;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.TrackCommand;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectLinkException;

import java.net.URI;

@Component
public final class TrackCommandHandler extends LinkCommandHandler {

    public TrackCommandHandler(
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig,
            ScrapperClient scrapperClient,
            final LinkParserService linkParserService
    ) {
        super(userResponseService, applicationConfig, scrapperClient, linkParserService);
    }

    @Override
    public void handle(BotCommandArguments arguments) {
        try {
            super.handle(arguments);
        } catch (LinkParserIncorrectLinkException exception) {
            getUserResponseService().sendMessage(
                    arguments.userId(),
                    getApplicationConfig().command().common().message().invalidLink()
            );
        }
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof TrackCommand;
    }

    @Override
    protected void sendLinkToScrapper(URI link, Long userId) {
        getScrapperClient().addLink(userId, new AddLinkRequest(link));
    }

    @Override
    protected void sendSuccessMessage(Long userId) {
        getUserResponseService().sendMessage(userId, getApplicationConfig().command().track().message().success());
    }

    @Override
    protected String noLinkMessage() {
        return getApplicationConfig().command().track().message().noLink();
    }
}
