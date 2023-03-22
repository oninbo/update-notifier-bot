package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@Component
public class TrackCommandHandler extends LinkCommandHandler {
    @Value("${command.track.message.no_link}")
    private String noLinkMessage;

    public TrackCommandHandler(UserResponseService userResponseService) {
        super(userResponseService);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand == BotCommand.TRACK;
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
