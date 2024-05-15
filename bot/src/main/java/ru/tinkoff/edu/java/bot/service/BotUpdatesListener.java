package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import io.micrometer.core.instrument.Counter;
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
public final class BotUpdatesListener implements UpdatesListener {
    private final BotCommandService botCommandService;
    private final BotMenuButtonService botMenuButtonService;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;
    private final WebClientErrorHandler webClientErrorHandler;
    private final Counter telegramMessagesHandled;

    @Override
    public int process(List<Update> updates) {
        for (var update : updates) {
            try {
                processUpdate(update);
            } catch (WebClientResponseException exception) {
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
        log.info(new UpdateLog(update).toString());
        Optional.ofNullable(update.message())
                .ifPresent(this::processMessage);
    }

    private void processMessage(Message message) {
        if (Objects.nonNull(message.entities())) {
            Arrays.stream(message.entities())
                    .forEach(entity -> processMessageEntity(entity, message));
        } else {
            botMenuButtonService.handleMessage(message);
        }
        telegramMessagesHandled.increment();
    }

    private void processMessageEntity(MessageEntity messageEntity, Message message) {
        if (Objects.isNull(messageEntity.type())) {
            return;
        }
        switch (messageEntity.type()) {
            case bot_command -> botCommandService.handleCommandEntity(message, messageEntity);
            case url -> botMenuButtonService.handleMessage(message);
            default -> {
            }
        }
    }

    private void sendErrorMessage(Long userId) {
        userResponseService.sendMessage(
                userId,
                applicationConfig.message().error()
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

