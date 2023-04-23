package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.log.SendResponseLog;

import java.util.Optional;

@Service
@Slf4j
public class UserResponseService {
    private final TgBot telegramBot;
    private final Keyboard keyboard;
    private final ApplicationConfig applicationConfig;

    public UserResponseService(TgBot telegramBot, TgKeyboardService tgKeyboardService, ApplicationConfig config) {
        this.telegramBot = telegramBot;
        this.keyboard = tgKeyboardService.createKeyboard();
        this.applicationConfig = config;
    }

    public void sendMessage(Long userId, String text) {
        sendMessage(userId, text, keyboard);
    }

    public void sendMessageForceReply(Long userId, String text) {
        sendMessage(userId, text, new ForceReply());
    }

    public boolean isMessageFromBot(Message message){
        return Optional.ofNullable(message.from())
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
            telegramBot.execute(new SendMessage(userId, applicationConfig.message().error()));
            log.error(SendResponseLog.fromSendResponse(response).toString());
        }
    }
}
