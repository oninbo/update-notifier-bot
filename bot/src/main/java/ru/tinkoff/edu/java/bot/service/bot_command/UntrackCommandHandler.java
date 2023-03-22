package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@SuppressWarnings("SpellCheckingInspection")
@Component
public class UntrackCommandHandler extends LinkCommandHandler {
    public UntrackCommandHandler(
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig
    ) {
        super(userResponseService, applicationConfig);
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
    protected void sendSuccessMessage(Long userId) {
        userResponseService.sendMessage(userId, applicationConfig.command().untrack().message().success());
    }

    @Override
    protected String noLinkMessage() {
        return applicationConfig.command().untrack().message().noLink();
    }
}
