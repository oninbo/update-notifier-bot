package ru.tinkoff.edu.java.scrapper.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;

@RequiredArgsConstructor
public class TgChatNotFoundException extends ServiceException {
    private final ApplicationConfig applicationConfig;

    @Override
    public String code() {
        return codeFromHttpStatus(HttpStatus.NOT_FOUND);
    }

    @Override
    public String description() {
        return applicationConfig.errorDescription().tgChatNotFound();
    }
}
