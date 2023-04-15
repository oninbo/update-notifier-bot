package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.LinkResponse;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.ListCommand;
import ru.tinkoff.edu.java.bot.utils.LinkParseResultPresenter;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

import java.net.URI;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ListCommandHandler implements BotCommandHandler {
    private final UserResponseService userResponseService;
    private final LinkParserService linkParserService;

    private final ApplicationConfig applicationConfig;

    private final ScrapperClient scrapperClient;

    @Override
    public void handle(BotCommandArguments arguments) {
        List<URI> links = getLinks(arguments.userId());
        if (links.isEmpty()) {
            String noLinksMessage = applicationConfig.command().list().message().noLinks();
            userResponseService.sendMessage(arguments.userId(), noLinksMessage);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(
                applicationConfig.command().list().header() + ":\n"
        );
        for (int i = 0; i < links.size(); i++) {
            stringBuilder.append(String.format("%d. ", i + 1));
            URI link = links.get(i);
            var linkParseResultPresenter = new LinkParseResultPresenter(stringBuilder, link.toString());
            linkParserService.parse(link).ifPresentOrElse(
                    linkParseResultPresenter::present,
                    () -> stringBuilder.append(link)
            );
            stringBuilder.append("\n");

        }
        userResponseService.sendMessage(arguments.userId(), stringBuilder.toString());
        // TODO: pagination
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof ListCommand;
    }

    private List<URI> getLinks(Long id) {
        return scrapperClient.getLinks(id)
                .links().stream()
                .map(LinkResponse::url)
                .toList();
    }
}

