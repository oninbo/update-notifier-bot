package ru.tinkoff.edu.java.bot.service.log;

import com.pengrad.telegrambot.response.SendResponse;

import java.util.Objects;

public record SendResponseLog(MessageLog message) {
    public static SendResponseLog fromSendResponse(SendResponse sendResponse) {
        if (Objects.isNull(sendResponse.message())) {
            return new SendResponseLog(null);
        }
        return new SendResponseLog(MessageLog.fromMessage(sendResponse.message()));
    }
}
