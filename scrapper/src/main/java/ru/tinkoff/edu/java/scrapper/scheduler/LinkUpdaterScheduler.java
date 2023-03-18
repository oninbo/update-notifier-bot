package ru.tinkoff.edu.java.scrapper.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;

@Component
public class LinkUpdaterScheduler {
    private final Logger logger;

    private final SchedulerConfig schedulerConfig;

    public LinkUpdaterScheduler(SchedulerConfig schedulerConfig) {
        this.schedulerConfig = schedulerConfig;
        logger = LoggerFactory.getLogger(LinkUpdaterScheduler.class);
    }

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    public void update() {
        logger.info(String.format("Update each %s seconds!", schedulerConfig.getInterval().toSeconds()));
    }
}
