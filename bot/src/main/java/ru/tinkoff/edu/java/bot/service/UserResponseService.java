package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResponseService {
    private final TgBot telegramBot;
    private final Logger logger;

    private final Keyboard keyboard;

    public UserResponseService(TgBot telegramBot, TgKeyboardService tgKeyboardService) {
        this.telegramBot = telegramBot;
        this.logger = LoggerFactory.getLogger(UserResponseService.class);
        this.keyboard = tgKeyboardService.createKeyboard();
    }

    public void sendMessage(Long userId, String text) {
        sendMessage(userId, text, keyboard);
    }

    public void sendMessageForceReply(Long userId, String text) {
        sendMessage(userId, text, new ForceReply());
    }

    public boolean isMessageFromBot(Message message){
        return Optional.of(message.from())
                .map(User::id)
                .map(id -> id.equals(telegramBot.getBotId()))
                .orElse(false);
    }

    private void sendMessage(Long userId, String text, Keyboard keyboard) {
        var request = new SendMessage(userId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.Markdown)
                .replyMarkup(keyboard);
        var response = telegramBot.execute(request);
        if (!response.isOk()) {
            logger.error(response.toString());
        }
    }
}