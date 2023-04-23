package ru.tinkoff.edu.java.scrapper.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
public record RemoveLinkRequest(@NotNull URI link) {
}
