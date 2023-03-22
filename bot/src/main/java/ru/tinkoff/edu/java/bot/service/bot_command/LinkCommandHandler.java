package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@RequiredArgsConstructor
public abstract class LinkCommandHandler implements BotCommandHandler {
    private final UserResponseService userResponseService;

    @Override
    public void handle(BotCommandArguments arguments) {
        String link = arguments.text().trim();
        if (link.isBlank()) {
            userResponseService.sendMessage(arguments.userId(), noLinkMessage());
        } else {
            sendLinkToScrapper(link, arguments.userId());
        }
    }

    protected abstract void sendLinkToScrapper(String link, Long userId);

    protected abstract String noLinkMessage();
}
