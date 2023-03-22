package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@SuppressWarnings("SpellCheckingInspection")
@Component
public class UntrackCommandHandler extends LinkCommandHandler {
    @Value("${command.untrack.message.no_link}")
    private String noLinkMessage;

    public UntrackCommandHandler(UserResponseService userResponseService) {
        super(userResponseService);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand == BotCommand.UNTRACK;
    }

    @Override
    protected void sendLinkToScrapper(String link, Long userId) {
        // TODO
    }

    @Override
    protected String noLinkMessage() {
        return noLinkMessage;
    }
}
