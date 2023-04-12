package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.GitHubRepositoryNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.GitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.LinksRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.GitHubIssuesService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkUpdateUtils;
import ru.tinkoff.edu.java.scrapper.service.UpdatesService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcGitHubRepositoriesService implements
        FindOrDoService<GitHubRepository, GitHubParserResult>,
        UpdatesService<GitHubRepository>,
        GitHubIssuesService {
    private final GitHubRepositoriesRepository gitHubRepositoriesRepository;
    private final LinksRepository linksRepository;
    private final ApplicationConfig applicationConfig;
    private final GitHubClient gitHubClient;

    @Override
    public GitHubRepository findOrThrow(GitHubParserResult findParams) {
        return find(findParams).orElseThrow(() -> new GitHubRepositoryNotFoundException(applicationConfig));
    }

    @Override
    public GitHubRepository findOrCreate(GitHubParserResult findParams) {
        return find(findParams).orElseGet(
                () -> create(
                        new GitHubRepositoryAddParams(
                                findParams.userName(),
                                findParams.projectName()
                        )
                )
        );
    }

    public Optional<GitHubRepository> find(GitHubParserResult findParams) {
        return gitHubRepositoriesRepository.find(findParams.userName(), findParams.projectName());
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

    @Override
    public List<LinkUpdate> getLinkUpdates(List<GitHubRepository> repositories) {
        return LinkUpdateUtils.getUpdates(
                repositories,
                this::fetchedUpdatedAt,
                linksRepository::findAllWithChatId,
                GitHubRepository::updatedAt,
                GitHubRepository::createdAt
        );
    }

    @Override
    public void updateUpdatedAt(List<GitHubRepository> repos, OffsetDateTime updatedAt) {
        gitHubRepositoriesRepository.updateUpdatedAt(repos, updatedAt);
    }


    @Override
    public List<GitHubRepository> getObjectsForUpdate(int first) {
        return gitHubRepositoriesRepository.findAllWithLinks(first);
    }

    private OffsetDateTime fetchedUpdatedAt(GitHubRepository gitHubRepository) {
        GitHubRepositoryResponse response = gitHubClient.getRepository(
                gitHubRepository.username(),
                gitHubRepository.name(),
                applicationConfig.webClient().github().apiVersion()
        );
        return ObjectUtils.max(response.updatedAt(), response.pushedAt());
    }

    @Override
    public List<GitHubIssueUpdate> getGitHubIssueUpdates(List<GitHubRepository> repositories) {
        List<GitHubIssueUpdate> updates = new ArrayList<>();
        for (var repo : repositories) {
            var issues = gitHubClient.getRepositoryIssues(
                    repo.username(),
                    repo.name(),
                    applicationConfig.webClient().github().apiVersion(),
                    repo.updatedAt().toString()
            );

            if (issues.isEmpty()) {
                continue;
            }

            var links = linksRepository.findAllWithChatId(repo);

            issues.forEach(
                    issue -> updates.add(
                            new GitHubIssueUpdate(
                                    issue.url(),
                                    new GitHubIssueUpdate.GitHubRepository(
                                            issue.repositoryUrl(),
                                            repo.name(),
                                            repo.username()
                                    ),
                                    links.stream().map(LinkWithChatId::chatId).toList()
                            )
                    )
            );
        }
        return updates;
    }
}
