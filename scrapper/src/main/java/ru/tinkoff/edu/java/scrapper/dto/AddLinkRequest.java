package ru.tinkoff.edu.java.scrapper.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record AddLinkRequest(@NotBlank String link) {
}
