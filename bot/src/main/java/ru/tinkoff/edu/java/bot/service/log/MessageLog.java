package ru.tinkoff.edu.java.bot.service.log;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.User;

import java.util.Arrays;

public record MessageLog(User from, String text, MessageEntity[] entities) {
    public MessageLog(Message message) {
        this(message.from(), message.text(), message.entities());
    }

    @Override
    public String toString() {
        return "MessageLog{" +
                "text='" + text + '\'' +
                ", from=" + from +
                ", entities=" + Arrays.toString(entities) +
                '}';
    }
}
