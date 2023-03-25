package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectURLException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class LinkCommandHandler implements BotCommandHandler {
    protected final UserResponseService userResponseService;
    protected final ApplicationConfig applicationConfig;
    protected final ScrapperClient scrapperClient;
    private final LinkParserService linkParserService;

    @Override
    public void handle(BotCommandArguments arguments) {
        String link = arguments.text().trim();
        if (link.isBlank()) {
            userResponseService.sendMessage(arguments.userId(), noLinkMessage());
        } else {
            try {
                if (tryParseLink(link).isEmpty()) {
                    userResponseService.sendMessage(
                            arguments.userId(),
                            applicationConfig.command().common().message().unsupportedLink()
                    );
                    return;
                }

                sendLinkToScrapper(link, arguments.userId());
                sendSuccessMessage(arguments.userId());
            } catch (URISyntaxException | MalformedURLException | LinkParserIncorrectURLException e) {
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

    protected Optional<LinkParserResult> tryParseLink(String link) throws MalformedURLException {
        try {
            return linkParserService.parse(link);
        } catch (RuntimeException exception) {
            if (exception.getCause() instanceof MalformedURLException) {
                throw (MalformedURLException) exception.getCause();
            } else {
                throw exception;
            }
        }
    }
}
