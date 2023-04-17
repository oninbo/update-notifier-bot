package ru.tinkoff.edu.java.scrapper.configuration.access_config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqGitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.LinksService;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;
import ru.tinkoff.edu.java.scrapper.service.jooq.JooqGitHubRepositoriesService;
import ru.tinkoff.edu.java.scrapper.service.jooq.JooqLinksService;
import ru.tinkoff.edu.java.scrapper.service.jooq.JooqStackOverflowQuestionsService;
import ru.tinkoff.edu.java.scrapper.service.jooq.JooqTgChatsService;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
@RequiredArgsConstructor
public class JooqAccessConfiguration {
    private final JooqLinksRepository linksRepository;
    private final JooqTgChatsRepository tgChatsRepository;
    private final JooqStackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final JooqGitHubRepositoriesRepository gitHubRepositoriesRepository;

    private final DataAccessCommonDependencies commonDependencies;

    @Bean
    public LinksService jooqLinksService(
            JooqGitHubRepositoriesService gitHubRepositoriesService,
            JooqStackOverflowQuestionsService stackOverflowQuestionsService
    ) {
        return new JooqLinksService(
                linksRepository,
                tgChatsRepository,
                commonDependencies.applicationConfig(),
                stackOverflowQuestionsService,
                gitHubRepositoriesService,
                commonDependencies.linkParserService()
        );
    }

    @Bean
    public JooqGitHubRepositoriesService jooqGitHubRepositoriesService() {
        return new JooqGitHubRepositoriesService(
                gitHubRepositoriesRepository,
                linksRepository,
                commonDependencies.applicationConfig(),
                commonDependencies.gitHubClient()
        );
    }

    @Bean
    public JooqStackOverflowQuestionsService jooqStackOverflowQuestionsService() {
        return new JooqStackOverflowQuestionsService(
                stackOverflowQuestionsRepository,
                linksRepository,
                commonDependencies.applicationConfig(),
                commonDependencies.stackOverflowClient()
        );
    }

    @Bean
    public TgChatsService tgChatsService() {
        return new JooqTgChatsService(
                tgChatsRepository,
                commonDependencies.applicationConfig()
        );
    }
}
