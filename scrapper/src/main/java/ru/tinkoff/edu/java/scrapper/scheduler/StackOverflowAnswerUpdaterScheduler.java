package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.client.BotClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class StackOverflowAnswerUpdaterScheduler {
    @SuppressWarnings("unused")
    private final SchedulerConfig schedulerConfig;
    private final ApplicationConfig applicationConfig;
    private final StackOverflowAnswersService stackOverflowAnswersService;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    @Transactional
    public void update() {
        var questions = stackOverflowAnswersService.getForAnswersUpdate(applicationConfig.scheduler().batchSize());
        var updates = stackOverflowAnswersService.getStackOverflowAnswerUpdates(questions);
        stackOverflowAnswersService.updateAnswersUpdatedAt(questions, OffsetDateTime.now());
        updates.forEach(botClient::sendStackOverflowAnswerUpdates);
    }
}
