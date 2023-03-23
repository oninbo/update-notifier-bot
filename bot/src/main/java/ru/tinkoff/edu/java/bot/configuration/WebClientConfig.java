package ru.tinkoff.edu.java.bot.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record WebClientConfig(@NotBlank String baseUrl, String apiVersion) {
}
