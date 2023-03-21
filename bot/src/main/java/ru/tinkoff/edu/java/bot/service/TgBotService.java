package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TgBotService {
    private final TelegramBot telegramBot;
    private final UpdatesListener updatesListener;
    public void listen() {
        telegramBot.setUpdatesListener(updatesListener);
    }
}
