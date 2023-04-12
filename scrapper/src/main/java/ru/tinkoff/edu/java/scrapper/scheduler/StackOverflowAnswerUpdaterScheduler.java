package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.scrapper.client.BotClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;

@Component
@RequiredArgsConstructor
public class StackOverflowAnswerUpdaterScheduler {
    @SuppressWarnings("unused")
    private final SchedulerConfig schedulerConfig;
    private final ApplicationConfig applicationConfig;
    private final StackOverflowAnswersService stackOverflowAnswersService;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    public void update() {
        var questions = stackOverflowAnswersService.getObjectsForUpdate(applicationConfig.scheduler().batchSize());
        var updates = stackOverflowAnswersService.getStackOverflowAnswerUpdates(questions);
        updates.forEach(botClient::sendStackOverflowAnswerUpdates);
    }
}
