package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.bot.client.WebClientErrorHandler;
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
    private final WebClientErrorHandler webClientErrorHandler;

    public BotUpdatesListener(
            BotCommandService botCommandService,
            BotMenuButtonService botMenuButtonService,
            UserResponseService userResponseService,
            ApplicationConfig applicationConfig,
            WebClientErrorHandler webClientErrorHandler
    ) {
        this.applicationConfig = applicationConfig;
        this.botMenuButtonService = botMenuButtonService;
        this.botCommandService = botCommandService;
        this.userResponseService = userResponseService;
        this.webClientErrorHandler = webClientErrorHandler;
        logger = LoggerFactory.getLogger(BotUpdatesListener.class);
    }

    @Override
    public int process(List<Update> updates) {
        for (var update : updates) {
            try {
                processUpdate(update);
            } catch (WebClientResponseException.NotFound exception) {
                webClientErrorHandler
                        .handleWebClientException(
                                exception,
                                update,
                                () -> handleException(exception, update)
                        );
            } catch (Exception exception) {
                handleException(exception, update);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        logger.info(update.toString());
        var message = Optional.ofNullable(update.message());

        message.map(Message::entities).ifPresentOrElse(
                messageEntities ->
                        Arrays.stream(messageEntities)
                                .forEach(entity -> processMessageEntity(entity, update.message())),
                () -> message.ifPresent(botMenuButtonService::handleMessage)
        );
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

    private void handleException(Exception exception, Update update) {
        logger.error(exception.toString());
        Optional.ofNullable(update.message())
                .map(Message::from)
                .map(User::id)
                .ifPresent(this::sendErrorMessage);
    }
}
