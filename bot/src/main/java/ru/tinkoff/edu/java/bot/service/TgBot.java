package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetMe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.Optional;

@Component
public class TgBot extends TelegramBot {
    private final Long botId;
    private final Logger logger;

    public TgBot(ApplicationConfig applicationConfig) {
        super(applicationConfig.botConfig().token());
        this.logger = LoggerFactory.getLogger(TgBot.class);
        this.botId = fetchBotId().orElseThrow();
    }

    public Long getBotId() {
        return botId;
    }

    private Optional<Long> fetchBotId() {
        var response = execute(new GetMe());
        if (response.isOk()) {
            return Optional.of(response.user().id());
        } else {
            logger.error(response.toString());
        }
        return Optional.empty();
    }
}