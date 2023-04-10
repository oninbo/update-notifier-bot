package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.exception.GitHubRepositoryNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.GitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcGitHubRepositoriesService implements FindOrDoService<GitHubRepository, GitHubParserResult> {
    private final GitHubRepositoriesRepository gitHubRepositoriesRepository;
    private final ApplicationConfig applicationConfig;
    private final GitHubClient gitHubClient;

    @Override
    public GitHubRepository findOrThrow(GitHubParserResult findParams) {
        return find(findParams).orElseThrow(() -> new GitHubRepositoryNotFoundException(applicationConfig));
    }

    @Override
    public GitHubRepository findOrCreate(GitHubParserResult findParams) {
        return find(findParams).orElse(
                create(
                        new GitHubRepositoryAddParams(
                                findParams.userName(),
                                findParams.projectName()
                        )
                )
        );
    }

    public Optional<GitHubRepository> find(GitHubParserResult findParams) {
        return gitHubRepositoriesRepository.find(findParams.userName(), findParams.userName());
    }

    public GitHubRepository create(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        checkIfGitHubRepositoryExists(gitHubRepositoryAddParams);
        return gitHubRepositoriesRepository.add(gitHubRepositoryAddParams);
    }

    private void checkIfGitHubRepositoryExists(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        try {
            gitHubClient.getRepository(
                    gitHubRepositoryAddParams.username(),
                    gitHubRepositoryAddParams.name(),
                    applicationConfig.webClient().github().apiVersion()
            );
        } catch (WebClientResponseException.NotFound exception) {
            throw new GitHubRepositoryNotFoundException(applicationConfig);
        }
    }
}
