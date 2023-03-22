package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ListCommandHandler implements BotCommandHandler {
    private final UserResponseService userResponseService;
    private final LinkParserService linkParserService;

    private final ApplicationConfig applicationConfig;

    @Override
    public void handle(BotCommandArguments arguments) {
        List<String> links = getLinks();
        if (links.isEmpty()) {
            String noLinksMessage = applicationConfig.command().list().message().noLinks();
            userResponseService.sendMessage(arguments.userId(), noLinksMessage);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder("Отслеживаемые ссылки:\n");
        for (int i = 0; i < links.size(); i++) {
            stringBuilder.append(String.format("%d. ", i + 1));
            String link = links.get(i);
            var linkParseResultPresenter = new LinkParseResultPresenter(stringBuilder, link);
            linkParserService.parse(link).ifPresentOrElse(
                    linkParserResult -> linkParserResult.acceptVisitor(linkParseResultPresenter),
                    () -> stringBuilder.append(link)
            );
            stringBuilder.append("\n");

        }
        userResponseService.sendMessage(arguments.userId(), stringBuilder.toString());
        // TODO: pagination
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand == BotCommand.LIST;
    }

    private List<String> getLinks() {
        // TODO: get links from scrapper
        return List.of(
                "https://stackoverflow.com/questions/34088780",
                "https://github.com/sanyarnd/tinkoff-java-course-2022"
        );
    }
}

