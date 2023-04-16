package ru.tinkoff.edu.java.scrapper.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.GitHubRepositoryNotFoundException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class GitHubRepositoriesService {
    protected void checkIfGitHubRepositoryExists(
            GitHubRepositoryAddParams addParams,
            ApplicationConfig config,
            GitHubClient gitHubClient
    ) {
        getGitHubRepositoryResponse(addParams.username(), addParams.name(), gitHubClient, config)
                .filter(this::isResponseValid)
                .orElseThrow(() -> new GitHubRepositoryNotFoundException(config));
    }

    /**
     * Метод для проверки ответа от API в случае, когда запрос вернул код 301 и тело без полей репозитория
     *
     * @param response ответ от API
     * @return true когда ответ содержит нужные поля, иначе false
     */
    protected boolean isResponseValid(GitHubRepositoryResponse response) {
        return ObjectUtils.allNotNull(response.name(), response.owner());
    }

    protected Optional<GitHubRepositoryResponse> getGitHubRepositoryResponse(
            String username,
            String name,
            GitHubClient gitHubClient,
            ApplicationConfig config
    ) {
        try {
            GitHubRepositoryResponse response = gitHubClient.getRepository(
                    username,
                    name,
                    config.webClient().github().apiVersion()
            );
            return Optional.of(response);
        } catch (WebClientResponseException.NotFound exception) {
            return Optional.empty();
        }
    }

    protected List<GitHubIssueUpdate> getGitHubIssueUpdates(
            List<GitHubRepository> repositories,
            BiFunction<GitHubRepository, OffsetDateTime, List<LinkWithChatId>> getLinks,
            GitHubClient gitHubClient,
            ApplicationConfig applicationConfig
    ) {
        List<GitHubIssueUpdate> updates = new ArrayList<>();
        for (var repo : repositories) {
            var issues = getRepositoryIssues(repo, gitHubClient, applicationConfig);

            if (issues.isEmpty()) {
                continue;
            }

            issues.forEach(
                    issue -> {
                        var links = getLinks.apply(repo, issue.createdAt());
                        if (links.isEmpty()) {
                            return;
                        }
                        var link = links.get(0);
                        updates.add(
                            new GitHubIssueUpdate(
                                    issue.url(),
                                    new GitHubIssueUpdate.GitHubRepository(
                                            link.url(),
                                            repo.name(),
                                            repo.username()
                                    ),
                                    links.stream().map(LinkWithChatId::chatId).toList()
                            )
                    );
                    }
            );
        }
        return updates;
    }

    protected OffsetDateTime fetchedUpdatedAt(
            GitHubRepository gitHubRepository,
            GitHubClient gitHubClient,
            ApplicationConfig applicationConfig
    ) {
        var responseResult = getGitHubRepositoryResponse(
                gitHubRepository.username(),
                gitHubRepository.name(),
                gitHubClient,
                applicationConfig);
        return responseResult
                .map(response -> ObjectUtils.max(response.updatedAt(), response.pushedAt()))
                .orElse(gitHubRepository.updatedAt()); // Если репозиторий не существует, так как был удален

        // FIXME: Было бы неплохо еще как-то редирект обрабатывать, возможно, через HttpClient с помощью
        // метода followRedirect(boolean), либо сделать Scheduler, который будет менять устаревшие ссылки на новые
    }

    private List<GitHubIssueResponse> getRepositoryIssues(
            GitHubRepository repo,
            GitHubClient gitHubClient,
            ApplicationConfig applicationConfig
    ) {
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
}
