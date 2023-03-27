package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetMe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.Optional;

@Component
@Slf4j
public class TgBot extends TelegramBot {
    private final Long botId;

    public TgBot(ApplicationConfig applicationConfig) {
        super(applicationConfig.botConfig().token());
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
            log.error(response.toString());
        }
        return Optional.empty();
    }
}
