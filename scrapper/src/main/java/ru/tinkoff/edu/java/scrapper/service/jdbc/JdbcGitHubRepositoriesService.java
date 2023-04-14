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
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcGitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcLinksRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.GitHubIssuesService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkUpdateUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
// TODO: change scope to default
public class JdbcGitHubRepositoriesService implements
        FindOrDoService<GitHubRepository, GitHubParserResult>,
        GitHubIssuesService {
    private final JdbcGitHubRepositoriesRepository jdbcGitHubRepositoriesRepository;
    private final JdbcLinksRepository jdbcLinksRepository;
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
        return jdbcGitHubRepositoriesRepository.find(findParams.userName(), findParams.projectName());
    }

    public GitHubRepository create(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        checkIfGitHubRepositoryExists(gitHubRepositoryAddParams);
        return jdbcGitHubRepositoriesRepository.add(gitHubRepositoryAddParams);
    }

    private void checkIfGitHubRepositoryExists(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        getGitHubRepositoryResponse(gitHubRepositoryAddParams.username(), gitHubRepositoryAddParams.name())
                .orElseThrow(() -> new GitHubRepositoryNotFoundException(applicationConfig));
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<GitHubRepository> repositories) {
        return LinkUpdateUtils.getUpdates(
                repositories,
                this::fetchedUpdatedAt,
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

    private OffsetDateTime fetchedUpdatedAt(GitHubRepository gitHubRepository) {
        return getGitHubRepositoryResponse(gitHubRepository.username(), gitHubRepository.name())
                .map(response -> ObjectUtils.max(response.updatedAt(), response.pushedAt()))
                .orElse(gitHubRepository.updatedAt()); // Если репозиторий не существует, так как был удален

        // FIXME: Было бы неплохо еще как-то редирект обрабатывать, возможно, через HttpClient с помощью
        // метода followRedirect(boolean), либо сделать Scheduler, который будет менять устаревшие ссылки на новые
    }

    private Optional<GitHubRepositoryResponse> getGitHubRepositoryResponse(String username, String name) {
        try {
            GitHubRepositoryResponse response = gitHubClient.getRepository(
                    username,
                    name,
                    applicationConfig.webClient().github().apiVersion()
            );
            return Optional.of(response);
        } catch (WebClientResponseException.NotFound exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<GitHubIssueUpdate> getGitHubIssueUpdates(List<GitHubRepository> repositories) {
        List<GitHubIssueUpdate> updates = new ArrayList<>();
        for (var repo : repositories) {
            var issues = getRepositoryIssues(repo);

            if (issues.isEmpty()) {
                continue;
            }

            var links = jdbcLinksRepository.findAllWithChatId(repo);
            if (links.isEmpty()) {
                continue;
            }
            var link = links.get(0);

            issues.forEach(
                    issue -> updates.add(
                            new GitHubIssueUpdate(
                                    issue.url(),
                                    new GitHubIssueUpdate.GitHubRepository(
                                            link.url(),
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

    @Override
    public void updateIssuesUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt) {
        jdbcGitHubRepositoriesRepository.updateIssuesUpdatedAt(repositories, updatedAt);
    }

    @Override
    public List<GitHubRepository> getRepositoriesForUpdate(int first) {
        return jdbcGitHubRepositoriesRepository
                .findAllWithLinks(first, JdbcGitHubRepositoriesRepository.UpdateColumn.ISSUES_UPDATED_AT);
    }

    private List<GitHubIssueResponse> getRepositoryIssues(GitHubRepository repo) {
        List<GitHubIssueResponse> result = new ArrayList<>();
        List<GitHubIssueResponse> issues;
        int page = 1;
        int perPage = 100;
        do {
            try {
                issues = gitHubClient.getRepositoryIssues(
                                repo.username(),
                                repo.name(),
                                applicationConfig.webClient().github().apiVersion(),
                                repo.issuesUpdatedAt().toString(),
                                page,
                                perPage
                        )
                        .stream()
                        .filter(i -> Objects.isNull(i.pullRequest()))
                        .toList();
                result.addAll(issues);
                page++;
            } catch (WebClientResponseException.NotFound exception) {
                return List.of();
            }
        } while (issues.size() == perPage);
        return result;
    }

    public void updateAllTimestamps(GitHubRepository repository, OffsetDateTime value) {
        var repos = List.of(repository);
        updateUpdatedAt(repos, value);
        updateIssuesUpdatedAt(repos, value);
    }
}
