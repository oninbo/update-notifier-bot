package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;

@Service
@RequiredArgsConstructor
public class TgKeyboardService {
    private final ApplicationConfig config;

    public ReplyKeyboardMarkup createKeyboard() {
        String[][] rows = {
                { BotCommand.HELP.getDescription(config), BotCommand.TRACK.getDescription(config) },
                { BotCommand.LIST.getDescription(config), BotCommand.UNTRACK.getDescription(config) },
                { BotCommand.START.getDescription(config) }
        };
        return new ReplyKeyboardMarkup(rows);
    }
}
