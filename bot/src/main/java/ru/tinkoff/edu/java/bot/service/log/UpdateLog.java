package ru.tinkoff.edu.java.bot.service.log;

import com.pengrad.telegrambot.model.Update;

public record UpdateLog(Integer updateId, MessageLog message) {
    public UpdateLog(Update update) {
        this(update.updateId(), new MessageLog(update.message()));
    }
}
