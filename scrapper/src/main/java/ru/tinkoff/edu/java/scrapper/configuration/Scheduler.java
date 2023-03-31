package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
public record Scheduler(@NotNull HumanReadableDuration interval) {
}
