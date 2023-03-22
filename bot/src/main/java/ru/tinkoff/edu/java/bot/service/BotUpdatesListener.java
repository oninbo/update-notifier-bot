package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class BotUpdatesListener implements UpdatesListener {
    private final Logger logger;
    private final BotCommandService botCommandService;
    private final BotMenuButtonService botMenuButtonService;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;

    public BotUpdatesListener(
            BotCommandService botCommandService,
            BotMenuButtonService botMenuButtonService,
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig
    ) {
        this.applicationConfig = applicationConfig;
        this.botMenuButtonService = botMenuButtonService;
        this.botCommandService = botCommandService;
        this.userResponseService = userResponseService;
        logger = LoggerFactory.getLogger(BotUpdatesListener.class);
    }

    @Override
    public int process(List<Update> updates) {
        for (var update : updates) {
            try {
                processUpdate(update);
            } catch (Exception exception) {
                logger.error(exception.toString());
                Optional.ofNullable(update.message())
                        .map(Message::from)
                        .map(User::id)
                        .ifPresent(this::sendErrorMessage);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        logger.info(update.toString());
        var message = Optional.ofNullable(update.message());
        message
            .map(Message::entities)
            .ifPresent(messageEntities ->
                    Arrays.stream(messageEntities)
                            .forEach(entity -> processMessageEntity(entity, update.message()))
            );
        message.ifPresent(botMenuButtonService::handleMessage);
    }

    private void processMessageEntity(MessageEntity messageEntity, Message message) {
        if (messageEntity.type() == MessageEntity.Type.bot_command) {
            botCommandService.handleCommandEntity(message, messageEntity);
        }
    }

    private void sendErrorMessage(Long userId) {
        userResponseService.sendMessage(
                userId,
                applicationConfig.command().common().message().botError()
        );
    }
}
