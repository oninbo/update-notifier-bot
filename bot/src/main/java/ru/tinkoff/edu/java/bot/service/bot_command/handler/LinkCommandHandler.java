package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectURIException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Класс для обработки ссылки, которую отправляет пользователь как текстовое сообщение в боте
 */
@RequiredArgsConstructor
public abstract class LinkCommandHandler implements BotCommandHandler {
    protected final UserResponseService userResponseService;
    protected final ApplicationConfig applicationConfig;
    protected final ScrapperClient scrapperClient;
    private final LinkParserService linkParserService;

    /**
     * Обрабатывает данные из сообщения пользователя
     * @param arguments данные из сообщения пользователя
     */
    @Override
    public void handle(BotCommandArguments arguments) {
        String linkText = arguments.text().trim();
        if (linkText.isBlank()) {
            userResponseService.sendMessage(arguments.userId(), noLinkMessage());
        } else {
            try {
                URI link = new URI(linkText);
                if (tryParseLink(link).isEmpty()) {
                    userResponseService.sendMessage(
                            arguments.userId(),
                            applicationConfig.command().common().message().unsupportedLink()
                    );
                    return;
                }

                sendLinkToScrapper(link, arguments.userId());
                sendSuccessMessage(arguments.userId());
            } catch (URISyntaxException | LinkParserIncorrectURIException e) {
                userResponseService.sendMessage(
                        arguments.userId(),
                        applicationConfig.command().common().message().invalidLink()
                );
            }
        }
    }

    /**
     * Отправить ссылку в сервис scrapper
     * @param link ссылка
     * @param userId ID пользователя, который отправил ссылку
     */
    protected abstract void sendLinkToScrapper(URI link, Long userId);

    /**
     * Отправить сообщение об успехе обработки ссылки
     * @param userId ID пользователя, которому отправляется сообщение
     */
    protected abstract void sendSuccessMessage(Long userId);

    /**
     * Вернуть сообщение о том, что ссылка не предоставлена
     * @return текст сообщения
     */
    protected abstract String noLinkMessage();

    /**
     * Попытать распарсить ссылку
     * @param link ссылка
     * @return результат парсинга ссылки
     */
    protected Optional<LinkParserResult> tryParseLink(URI link) {
        return linkParserService.parse(link);
    }
}
