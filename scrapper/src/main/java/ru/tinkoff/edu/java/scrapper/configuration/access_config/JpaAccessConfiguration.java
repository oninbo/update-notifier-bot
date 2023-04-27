package ru.tinkoff.edu.java.scrapper.configuration.access_config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.mapper.GithubRepositoryMapper;
import ru.tinkoff.edu.java.scrapper.mapper.LinkMapper;
import ru.tinkoff.edu.java.scrapper.mapper.StackOverflowQuestionMapper;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaGitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.jpa.JpaGitHubRepositoriesService;
import ru.tinkoff.edu.java.scrapper.service.jpa.JpaLinksService;
import ru.tinkoff.edu.java.scrapper.service.jpa.JpaStackOverflowQuestionsService;
import ru.tinkoff.edu.java.scrapper.service.jpa.JpaTgChatsService;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@RequiredArgsConstructor
public class JpaAccessConfiguration {
    private final JpaLinksRepository linksRepository;
    private final JpaTgChatsRepository tgChatsRepository;
    private final JpaStackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final JpaGitHubRepositoriesRepository gitHubRepositoriesRepository;

    private final DataAccessCommonDependencies dependencies;
    private final StackOverflowQuestionMapper stackOverflowQuestionMapper;
    private final GithubRepositoryMapper githubRepositoryMapper;
    private final LinkMapper linkMapper;

    @Bean
    public JpaStackOverflowQuestionsService jpaStackOverflowQuestionsService() {
        return new JpaStackOverflowQuestionsService(
                stackOverflowQuestionsRepository,
                linksRepository,
                dependencies.applicationConfig(),
                dependencies.stackOverflowClient(),
                stackOverflowQuestionMapper
        );
    }

    @Bean
    public JpaGitHubRepositoriesService jpaGitHubRepositoriesService() {
        return new JpaGitHubRepositoriesService(
                gitHubRepositoriesRepository,
                linksRepository,
                dependencies.applicationConfig(),
                dependencies.gitHubClient(),
                githubRepositoryMapper
        );
    }

    @Bean
    public JpaTgChatsService jpaTgChatsService() {
        return new JpaTgChatsService(
                tgChatsRepository,
                dependencies.applicationConfig()
        );
    }

    @Bean
    public JpaLinksService jpaLinksService(
            JpaStackOverflowQuestionsService stackOverflowQuestionsService,
            JpaGitHubRepositoriesService gitHubRepositoriesService
    ) {
        return new JpaLinksService(
                linksRepository,
                tgChatsRepository,
                dependencies.applicationConfig(),
                stackOverflowQuestionsService,
                gitHubRepositoriesService,
                dependencies.linkParserService(),
                linkMapper
        );
    }
}
