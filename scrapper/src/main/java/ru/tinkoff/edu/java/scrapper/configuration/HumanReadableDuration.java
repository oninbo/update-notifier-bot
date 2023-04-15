package ru.tinkoff.edu.java.scrapper.configuration;

import java.time.Duration;

public class HumanReadableDuration {
    private final Duration duration;

    public HumanReadableDuration(String text) {
        String durationText = "PT" + text.trim().replace(" ", "").toUpperCase();
        duration = Duration.parse(durationText);
    }

    public Duration getDuration() {
        return duration;
    }
}
