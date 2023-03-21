package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserResponseService {
    private final TelegramBot telegramBot;

    public Sender to(User user) {
        return new Sender(user);
    }

    @RequiredArgsConstructor
    public class Sender {
        private final User user;

        public void sendMessage(String text) {
            telegramBot.execute(new SendMessage(user.id(), text));
        }
    }
}
