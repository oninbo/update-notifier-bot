package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserResponseService {
    private final TelegramBot telegramBot;
    private final Logger logger;

    public UserResponseService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.logger = LoggerFactory.getLogger(UserResponseService.class);
    }

    public void sendMessage(Long userId, String text) {
        var request = new SendMessage(userId, text).disableWebPagePreview(true).parseMode(ParseMode.Markdown);
        var response = telegramBot.execute(request);
        if (!response.isOk()) {
            logger.error(response.toString());
        }
    }
}
