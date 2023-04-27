package ru.tinkoff.edu.java.scrapper.configuration.access_config;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;

@Component
public record DataAccessCommonDependencies(
        ApplicationConfig applicationConfig,
        LinkParserService linkParserService,

        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient
) {
}
