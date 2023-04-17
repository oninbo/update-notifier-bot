package ru.tinkoff.edu.java.scrapper.configuration.access_config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcGitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.LinksService;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;
import ru.tinkoff.edu.java.scrapper.service.jdbc.JdbcGitHubRepositoriesService;
import ru.tinkoff.edu.java.scrapper.service.jdbc.JdbcLinksService;
import ru.tinkoff.edu.java.scrapper.service.jdbc.JdbcStackOverflowQuestionsService;
import ru.tinkoff.edu.java.scrapper.service.jdbc.JdbcTgChatsService;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
@RequiredArgsConstructor
public class JdbcAccessConfiguration {
    private final JdbcLinksRepository linksRepository;
    private final JdbcTgChatsRepository tgChatsRepository;
    private final JdbcStackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final JdbcGitHubRepositoriesRepository gitHubRepositoriesRepository;

    private final DataAccessCommonDependencies commonDependencies;

    @Bean
    public LinksService jdbcLinksService(
            JdbcGitHubRepositoriesService gitHubRepositoriesService,
            JdbcStackOverflowQuestionsService stackOverflowQuestionsService
    ) {
        return new JdbcLinksService(
                linksRepository,
                tgChatsRepository,
                commonDependencies.applicationConfig(),
                commonDependencies.linkParserService(),
                gitHubRepositoriesService,
                stackOverflowQuestionsService
        );
    }

    @Bean
    public JdbcGitHubRepositoriesService jdbcGitHubRepositoriesService() {
        return new JdbcGitHubRepositoriesService(
                gitHubRepositoriesRepository,
                linksRepository,
                commonDependencies.applicationConfig(),
                commonDependencies.gitHubClient()
        );
    }

    @Bean
    public JdbcStackOverflowQuestionsService jdbcStackOverflowQuestionsService() {
        return new JdbcStackOverflowQuestionsService(
                stackOverflowQuestionsRepository,
                linksRepository,
                commonDependencies.stackOverflowClient(),
                commonDependencies.applicationConfig()
        );
    }

    @Bean
    public TgChatsService tgChatsService() {
        return new JdbcTgChatsService(
                tgChatsRepository,
                commonDependencies.applicationConfig()
        );
    }
}
