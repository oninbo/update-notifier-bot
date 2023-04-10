package ru.tinkoff.edu.java.bot.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.ApiErrorResponse;
import ru.tinkoff.edu.java.bot.exception.LinkNotSupportedException;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private final ApplicationConfig config;

    public ControllerExceptionHandler(ApplicationConfig config) {
        this.config = config;
    }

    @ExceptionHandler(value = {
            LinkParserException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse apiErrorResponse(Exception ex) {
        return new ApiErrorResponse(
                config.errorDescription().api(),
                Integer.toString(HttpStatus.BAD_REQUEST.value()),
                ex
        );
    }

    @ExceptionHandler(value = {
            LinkNotSupportedException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse linkNotSupportedResponse(Exception ex) {
        return new ApiErrorResponse(
                config.message().unsupportedLink(),
                Integer.toString(HttpStatus.BAD_REQUEST.value()),
                ex
        );
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse serverErrorResponse(Exception ex) {
        return new ApiErrorResponse(
                config.errorDescription().server(),
                Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                ex
        );
    }
}
