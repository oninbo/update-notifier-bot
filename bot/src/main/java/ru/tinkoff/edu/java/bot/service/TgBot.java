package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

@Component
public class TgBot extends TelegramBot {
    public TgBot(ApplicationConfig applicationConfig) {
        super(applicationConfig.botConfig().token());
    }
}
