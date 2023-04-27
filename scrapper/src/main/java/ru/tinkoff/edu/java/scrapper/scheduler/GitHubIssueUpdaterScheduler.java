package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.client.BotClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;
import ru.tinkoff.edu.java.scrapper.service.GitHubIssuesService;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class GitHubIssueUpdaterScheduler {
    @SuppressWarnings("unused")
    private final SchedulerConfig schedulerConfig;
    private final ApplicationConfig applicationConfig;
    private final GitHubIssuesService gitHubIssuesService;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    @Transactional
    public void update() {
        var repositories = gitHubIssuesService.getForIssuesUpdate(applicationConfig.scheduler().batchSize());
        var updates = gitHubIssuesService.getGitHubIssueUpdates(repositories);
        gitHubIssuesService.updateIssuesUpdatedAt(repositories, OffsetDateTime.now());
        updates.forEach(botClient::sendGithubIssueUpdates);
    }
}
