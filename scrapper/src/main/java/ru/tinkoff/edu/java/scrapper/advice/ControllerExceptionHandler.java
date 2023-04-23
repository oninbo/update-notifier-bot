package ru.tinkoff.edu.java.scrapper.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.ApiErrorResponse;
import ru.tinkoff.edu.java.scrapper.exception.ServiceException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final ApplicationConfig config;

    @ExceptionHandler(value = {
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

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse serverErrorResponse(Exception ex) {
        return new ApiErrorResponse(
                config.errorDescription().server(),
                Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                ex
        );
    }

    @ExceptionHandler(value = {TgChatNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiErrorResponse tgChatNotFoundErrorResponse(ServiceException ex) {
        return new ApiErrorResponse(
                ex.description(),
                ex.code(),
                ex
        );
    }

    @ExceptionHandler(value = {ServiceException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse serviceErrorResponse(ServiceException ex) {
        return new ApiErrorResponse(
                ex.description(),
                ex.code(),
                ex
        );
    }
}
