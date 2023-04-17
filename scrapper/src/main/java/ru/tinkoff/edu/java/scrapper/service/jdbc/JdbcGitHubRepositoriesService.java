package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.GitHubRepositoryNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcGitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcLinksRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.GitHubIssuesService;
import ru.tinkoff.edu.java.scrapper.service.GitHubRepositoriesService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkUpdateUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class JdbcGitHubRepositoriesService
        extends GitHubRepositoriesService
        implements FindOrDoService<GitHubRepository, GitHubParserResult>,
        GitHubIssuesService {
    private final JdbcGitHubRepositoriesRepository jdbcGitHubRepositoriesRepository;
    private final JdbcLinksRepository jdbcLinksRepository;
    private final ApplicationConfig applicationConfig;
    private final GitHubClient gitHubClient;

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
        return jdbcGitHubRepositoriesRepository.find(findParams.userName(), findParams.projectName());
    }

    public GitHubRepository create(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        checkIfGitHubRepositoryExists(gitHubRepositoryAddParams, applicationConfig, gitHubClient);
        return jdbcGitHubRepositoriesRepository.add(gitHubRepositoryAddParams);
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<GitHubRepository> repositories) {
        return LinkUpdateUtils.getUpdates(
                repositories,
                gitHubRepository -> fetchedUpdatedAt(gitHubRepository, gitHubClient, applicationConfig),
                jdbcLinksRepository::findAllWithChatId,
                GitHubRepository::updatedAt,
                GitHubRepository::createdAt
        );
    }

    @Override
    public void updateUpdatedAt(List<GitHubRepository> repos, OffsetDateTime updatedAt) {
        jdbcGitHubRepositoriesRepository.updateUpdatedAt(repos, updatedAt);
    }


    @Override
    public List<GitHubRepository> getForLinksUpdate(int first) {
        return jdbcGitHubRepositoriesRepository
                .findAllWithLinks(first, JdbcGitHubRepositoriesRepository.UpdateColumn.UPDATED_AT);
    }

    @Override
    public List<GitHubIssueUpdate> getGitHubIssueUpdates(List<GitHubRepository> repositories) {
        return getGitHubIssueUpdates(
                repositories,
                jdbcLinksRepository::findAllWithChatId,
                gitHubClient,
                applicationConfig
        );
    }

    @Override
    public void updateIssuesUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt) {
        jdbcGitHubRepositoriesRepository.updateIssuesUpdatedAt(repositories, updatedAt);
    }

    @Override
    public List<GitHubRepository> getForIssuesUpdate(int first) {
        return jdbcGitHubRepositoriesRepository
                .findAllWithLinks(first, JdbcGitHubRepositoriesRepository.UpdateColumn.ISSUES_UPDATED_AT);
    }
}
