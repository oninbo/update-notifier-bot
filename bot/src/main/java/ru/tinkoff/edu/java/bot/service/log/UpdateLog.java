package ru.tinkoff.edu.java.bot.service.log;

import com.pengrad.telegrambot.model.Update;

public record UpdateLog(Integer updateId, MessageLog message) {
    public static UpdateLog fromUpdate(Update update) {
        return new UpdateLog(update.updateId(), MessageLog.fromMessage(update.message()));
    }
}
