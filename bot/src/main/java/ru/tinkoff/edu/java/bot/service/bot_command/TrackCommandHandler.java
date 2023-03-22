package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@Component
public class TrackCommandHandler extends LinkCommandHandler {
    public TrackCommandHandler(
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig
    ) {
        super(userResponseService, applicationConfig);
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
    protected void sendSuccessMessage(Long userId) {
        userResponseService.sendMessage(userId, applicationConfig.command().track().message().success());
    }

    @Override
    protected String noLinkMessage() {
        return applicationConfig.command().track().message().noLink();
    }
}
