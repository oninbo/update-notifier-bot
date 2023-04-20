package ru.tinkoff.edu.java.scrapper.service.jpa;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.GitHubIssueUpdate;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.exception.GitHubRepositoryNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaGitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaLinksRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.GitHubIssuesService;
import ru.tinkoff.edu.java.scrapper.service.GitHubRepositoriesService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkUpdateUtils;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class JpaGitHubRepositoriesService
        extends GitHubRepositoriesService
        implements FindOrDoService<GitHubRepository, GitHubParserResult>,
        GitHubIssuesService {
    private final JpaGitHubRepositoriesRepository gitHubRepositoriesRepository;
    private final JpaLinksRepository linksRepository;
    private final ApplicationConfig applicationConfig;
    private final GitHubClient gitHubClient;

    @Override
    public GitHubRepository findOrThrow(GitHubParserResult findParams) {
        return gitHubRepositoriesRepository.find(findParams)
                .orElseThrow(() -> new GitHubRepositoryNotFoundException(applicationConfig));
    }

    @Override
    public GitHubRepository findOrCreate(GitHubParserResult findParams) {
        return gitHubRepositoriesRepository.find(findParams)
                .orElseGet(() -> {
                    var addParams = new GitHubRepositoryAddParams(
                            findParams.userName(),
                            findParams.projectName()
                    );
                    checkIfGitHubRepositoryExists(addParams, applicationConfig, gitHubClient);
                    return gitHubRepositoriesRepository.add(addParams);
                });
    }

    @Override
    public List<GitHubIssueUpdate> getGitHubIssueUpdates(List<GitHubRepository> repositories) {
        return getGitHubIssueUpdates(
                repositories,
                linksRepository::findAllWithChatId,
                gitHubClient,
                applicationConfig
        );
    }

    @Override
    public void updateIssuesUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt) {

        // TODO add map struct mapper
//        gitHubRepositoriesRepository.update(repositories, GITHUB_REPOSITORIES.ISSUES_UPDATED_AT, updatedAt);
    }

    @Override
    public List<GitHubRepository> getForIssuesUpdate(int first) {
        return gitHubRepositoriesRepository
                .findAllWithLinks(first, JpaGitHubRepositoriesRepository.OrderColumn.issuesUpdatedAt);
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<GitHubRepository> repositories) {
        return LinkUpdateUtils.getUpdates(
                repositories,
                gitHubRepository -> fetchedUpdatedAt(gitHubRepository, gitHubClient, applicationConfig),
                linksRepository::findAllWithChatId,
                GitHubRepository::updatedAt,
                GitHubRepository::createdAt
        );
    }

    @Override
    public void updateUpdatedAt(List<GitHubRepository> repos, OffsetDateTime updatedAt) {
        // TODO add map struct mapper
//        gitHubRepositoriesRepository.update(repos, GITHUB_REPOSITORIES.UPDATED_AT, updatedAt);
    }

    @Override
    public List<GitHubRepository> getForLinksUpdate(int first) {
        return gitHubRepositoriesRepository
                .findAllWithLinks(first, JpaGitHubRepositoriesRepository.OrderColumn.updatedAt);
    }
}
