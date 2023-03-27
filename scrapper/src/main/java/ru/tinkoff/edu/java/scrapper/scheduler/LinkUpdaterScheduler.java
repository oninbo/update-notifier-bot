package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;

@Component
@Slf4j
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    private final SchedulerConfig schedulerConfig;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    public void update() {
        log.info(String.format("Update each %s seconds!", schedulerConfig.getInterval().toSeconds()));
    }
}
