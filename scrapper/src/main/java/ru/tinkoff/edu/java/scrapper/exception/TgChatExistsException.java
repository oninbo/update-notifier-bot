package ru.tinkoff.edu.java.scrapper.exception;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;

@RequiredArgsConstructor
public class TgChatExistsException extends ServiceException {
    private final ApplicationConfig applicationConfig;

    @Override
    public String description() {
        return applicationConfig.errorDescription().tgChatExists();
    }
}
