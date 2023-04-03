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
        } catch (LinkParserIncorrectLinkException exception) {
            userResponseService.sendMessage(
                    arguments.userId(),
                    applicationConfig.command().common().message().invalidLink()
            );
        }
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof TrackCommand;
    }

    @Override
    protected void sendLinkToScrapper(URI link, Long userId) {
        scrapperClient.addLink(userId, new AddLinkRequest(link));
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
