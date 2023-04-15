package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.*;

@Service
@RequiredArgsConstructor
public class TgKeyboardService {
    private final ApplicationConfig config;
    private final ApplicationContext context;

    public ReplyKeyboardMarkup createKeyboard() {
        String[][] rows = {
                { getCommandDescription(HelpCommand.class), getCommandDescription(TrackCommand.class) },
                { getCommandDescription(ListCommand.class), getCommandDescription(UntrackCommand.class) },
                { getCommandDescription(StartCommand.class) }
        };
        return new ReplyKeyboardMarkup(rows);
    }

    private <T extends BotCommand> String getCommandDescription(Class<T> botClass) {
        return context.getBean(botClass).getDescription(config);
    }
}
