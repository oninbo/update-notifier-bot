package ru.tinkoff.edu.java.bot.client;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.bot.dto.ApiErrorResponse;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebClientErrorHandler {
    private final UserResponseService userResponseService;

    public void handleWebClientException(
            WebClientResponseException exception,
            Update update,
            Runnable onFailure) {
        var body = Optional.ofNullable(exception.getResponseBodyAs(ApiErrorResponse.class));
        body.ifPresentOrElse(
                response -> Optional.ofNullable(update.message())
                        .map(Message::from)
                        .map(User::id)
                        .ifPresent(
                                userId -> userResponseService.sendMessage(userId, response.description())
                        ),
                onFailure
        );
    }
}
