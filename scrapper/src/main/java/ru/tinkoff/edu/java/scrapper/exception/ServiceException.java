package ru.tinkoff.edu.java.scrapper.exception;

import org.springframework.http.HttpStatus;

public abstract class ServiceException extends RuntimeException {
    /**
     * Возвращает HTTP код, который нужно вернуть при обработке исключения.
     *
     * @return HTTP код
     */
    public String code() {
        return codeFromHttpStatus(HttpStatus.BAD_REQUEST);
    }

    public abstract String description();

    protected final String codeFromHttpStatus(HttpStatus httpStatus) {
        return Integer.toString(httpStatus.value());
    }
}
