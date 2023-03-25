package ru.tinkoff.edu.java.bot.client;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.bot.dto.ApiErrorResponse;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

import java.util.Optional;

@Component
public class WebClientErrorHandler {
    private final UserResponseService userResponseService;
    private final Logger logger;

    public WebClientErrorHandler(UserResponseService userResponseService) {
        this.userResponseService = userResponseService;
        this.logger = LoggerFactory.getLogger(WebClientErrorHandler.class);
    }

    public void handleWebClientException(
            WebClientResponseException exception,
            Update update,
            Runnable onFailure) {
        var body = Optional.ofNullable(exception.getResponseBodyAs(ApiErrorResponse.class));
        body.ifPresentOrElse(
                response -> {
                    try {
                        handleResponseBody(response, update);
                    } catch (Exception e) {
                        logger.error(e.toString());
                        onFailure.run();
                    }

                },
                onFailure
        );
    }

    private void handleResponseBody(ApiErrorResponse response, Update update) {
        Optional.ofNullable(update.message())
                .map(Message::from)
                .map(User::id)
                .ifPresent(
                        userId -> userResponseService.sendMessage(userId, response.description())
                );
    }
}
