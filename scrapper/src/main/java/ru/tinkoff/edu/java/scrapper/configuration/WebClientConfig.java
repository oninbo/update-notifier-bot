package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record WebClientConfig(@NotBlank String baseUrl, @NotBlank String apiVersion) {
}
