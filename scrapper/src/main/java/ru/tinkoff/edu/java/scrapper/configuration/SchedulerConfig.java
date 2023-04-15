package ru.tinkoff.edu.java.scrapper.configuration;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SchedulerConfig {
    private final Duration interval;

    public SchedulerConfig(ApplicationConfig applicationConfig) {
        this.interval = applicationConfig.scheduler().interval().getDuration();
    }

    public Duration getInterval() {
        return interval;
    }
}
