package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TgBotService {
    private final TelegramBot telegramBot;
    private final UpdatesListener updatesListener;
    private final ApplicationConfig applicationConfig;
    private final List<BotCommand> botCommands;

    public void listen() {
        setCommands(applicationConfig);
        telegramBot.setUpdatesListener(updatesListener);
    }

    private void setCommands(ApplicationConfig applicationConfig) {
        var response = telegramBot.execute(
                new SetMyCommands(BotCommand.getTgCommands(applicationConfig, botCommands))
        );
        if (!response.isOk()) {
            log.error(response.toString());
        }
    }
}
