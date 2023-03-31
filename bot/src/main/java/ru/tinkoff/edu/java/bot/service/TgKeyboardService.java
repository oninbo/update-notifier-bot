package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;

@Service
@RequiredArgsConstructor
public class TgKeyboardService {
    private final ApplicationConfig config;
    private final ApplicationContext context;

    public ReplyKeyboardMarkup createKeyboard() {
        @SuppressWarnings("SpellCheckingInspection") String[][] rows = {
                { getCommand("help").getDescription(config), getCommand("track").getDescription(config) },
                { getCommand("list").getDescription(config), getCommand("untrack").getDescription(config) },
                { getCommand("start").getDescription(config) }
        };
        return new ReplyKeyboardMarkup(rows);
    }

    private BotCommand getCommand(String name) {
        return context.getBean(name, BotCommand.class);
    }
}
