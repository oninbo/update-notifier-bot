package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;

@Service
public class TgBotService {
    private final TelegramBot telegramBot;
    private final UpdatesListener updatesListener;

    private final ApplicationConfig applicationConfig;

    private final Logger logger;

    public TgBotService(
            TelegramBot telegramBot,
            UpdatesListener updatesListener,
            ApplicationConfig applicationConfig
    ) {
        this.applicationConfig = applicationConfig;
        this.telegramBot = telegramBot;
        this.updatesListener = updatesListener;
        logger = LoggerFactory.getLogger(TgBotService.class);
    }

    public void listen() {
        setCommands(applicationConfig);
        telegramBot.setUpdatesListener(updatesListener);
    }

    private void setCommands(ApplicationConfig applicationConfig) {
        var response = telegramBot.execute(new SetMyCommands(BotCommand.getTgCommands(applicationConfig)));
        if (!response.isOk()) {
            logger.error(response.toString());
        }
    }
}
