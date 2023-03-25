package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.AddLinkRequest;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectURLException;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class TrackCommandHandler extends LinkCommandHandler {

    public TrackCommandHandler(
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig,
            ScrapperClient scrapperClient,
            LinkParserService linkParserService
    ) {
        super(userResponseService, applicationConfig, scrapperClient, linkParserService);
    }

    @Override
    public void handle(BotCommandArguments arguments) {
        try {
            super.handle(arguments);
        } catch (LinkParserIncorrectURLException exception) {
            userResponseService.sendMessage(
                    arguments.userId(),
                    applicationConfig.command().common().message().invalidLink()
            );
        }
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand == BotCommand.TRACK;
    }

    @Override
    protected void sendLinkToScrapper(String link, Long userId) throws URISyntaxException {
        scrapperClient.addLink(userId, new AddLinkRequest(new URI(link)));
    }

    @Override
    protected void sendSuccessMessage(Long userId) {
        userResponseService.sendMessage(userId, applicationConfig.command().track().message().success());
    }

    @Override
    protected String noLinkMessage() {
        return applicationConfig.command().track().message().noLink();
    }
}
