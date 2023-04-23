package ru.tinkoff.edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record Scheduler(@NotNull HumanReadableDuration interval, @NotNull Integer batchSize) {
}
