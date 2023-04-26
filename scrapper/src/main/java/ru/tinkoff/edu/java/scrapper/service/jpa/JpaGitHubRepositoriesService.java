package ru.tinkoff.edu.java.scrapper.service.jpa;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.GitHubIssueUpdate;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.mapper.GithubRepositoryMapper;
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
        implements FindOrDoService<GitHubRepositoryEntity, GitHubParserResult>,
        GitHubIssuesService {
    private final JpaGitHubRepositoriesRepository gitHubRepositoriesRepository;
    private final JpaLinksRepository linksRepository;
    private final ApplicationConfig applicationConfig;
    private final GitHubClient gitHubClient;
    private final GithubRepositoryMapper mapper;

    @Override
    public GitHubRepositoryEntity findOrCreate(GitHubParserResult findParams) {
        return gitHubRepositoriesRepository.findByUsernameAndName(findParams.userName(), findParams.projectName())
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
        var ids = repositories.stream().map(GitHubRepository::id).toList();
        var entities = gitHubRepositoriesRepository.findAllById(ids);
        entities.forEach(e -> e.setIssuesUpdatedAt(updatedAt));
        gitHubRepositoriesRepository.saveAllAndFlush(entities);
    }

    @Override
    public List<GitHubRepository> getForIssuesUpdate(int first) {
        return gitHubRepositoriesRepository
                .findAllWithLinks(first, JpaGitHubRepositoriesRepository.OrderColumn.issuesUpdatedAt)
                .stream().map(mapper::fromEntity)
                .toList();
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
    public void updateUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt) {
        var ids = repositories.stream().map(GitHubRepository::id).toList();
        var entities = gitHubRepositoriesRepository.findAllById(ids);
        entities.forEach(e -> e.setUpdatedAt(updatedAt));
        gitHubRepositoriesRepository.saveAllAndFlush(entities);
    }

    @Override
    public List<GitHubRepository> getForLinksUpdate(int first) {
        return gitHubRepositoriesRepository
                .findAllWithLinks(first, JpaGitHubRepositoriesRepository.OrderColumn.updatedAt)
                .stream().map(mapper::fromEntity)
                .toList();
    }
}
