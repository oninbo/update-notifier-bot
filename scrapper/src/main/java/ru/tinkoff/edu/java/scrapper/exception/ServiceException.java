package ru.tinkoff.edu.java.scrapper.exception;

import org.springframework.http.HttpStatus;

public abstract class ServiceException extends RuntimeException {
    public String code() {
        return codeFromHttpStatus(HttpStatus.BAD_REQUEST);
    }

    public abstract String description();

    protected String codeFromHttpStatus(HttpStatus httpStatus) {
        return Integer.toString(httpStatus.value());
    }
}
