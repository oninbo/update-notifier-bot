package ru.tinkoff.edu.java.bot.dto;

import java.util.Arrays;
import java.util.List;

public record ApiErrorResponse(
        String description,
        String code,
        String exceptionName,

        String exceptionMessage,
        List<String> stacktrace
) {
    public ApiErrorResponse(String description, String code, Exception exception) {
        this(
            description,
            code,
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
