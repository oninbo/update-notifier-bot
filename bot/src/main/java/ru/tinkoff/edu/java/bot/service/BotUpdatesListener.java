package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.bot.client.WebClientErrorHandler;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.log.UpdateLog;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotUpdatesListener implements UpdatesListener {
    private final BotCommandService botCommandService;
    private final BotMenuButtonService botMenuButtonService;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;
    private final WebClientErrorHandler webClientErrorHandler;

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
        log.info(UpdateLog.fromUpdate(update).toString());
        var message = Optional.ofNullable(update.message());

        message.map(Message::entities).ifPresentOrElse(
                messageEntities ->
                        Arrays.stream(messageEntities)
                                .forEach(entity -> processMessageEntity(entity, update.message())),
                () -> message.ifPresent(botMenuButtonService::handleMessage)
        );
    }

    private void processMessageEntity(MessageEntity messageEntity, Message message) {
        if (Objects.isNull(messageEntity.type())) {
            return;
        }
        switch (messageEntity.type()) {
            case bot_command -> botCommandService.handleCommandEntity(message, messageEntity);
            case url -> botMenuButtonService.handleMessage(message);
        }
    }

    private void sendErrorMessage(Long userId) {
        userResponseService.sendMessage(
                userId,
                applicationConfig.command().common().message().botError()
        );
    }

    private void handleException(Exception exception, Update update) {
        log.error(exception.toString());
        Optional.ofNullable(update.message())
                .map(Message::from)
                .map(User::id)
                .ifPresent(this::sendErrorMessage);
    }
}

