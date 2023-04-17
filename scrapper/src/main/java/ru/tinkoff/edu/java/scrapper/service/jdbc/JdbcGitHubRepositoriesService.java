package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryResponse;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.exception.GitHubRepositoryNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.GitHubRepositoriesRepository;
import ru.tinkoff.edu.java.scrapper.repository.LinksRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkUpdateUtils;
import ru.tinkoff.edu.java.scrapper.service.UpdatesService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcGitHubRepositoriesService implements
        FindOrDoService<GitHubRepository, GitHubParserResult>,
        UpdatesService<GitHubRepository> {
    private final GitHubRepositoriesRepository gitHubRepositoriesRepository;
    private final LinksRepository linksRepository;
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
        return gitHubRepositoriesRepository.find(findParams.userName(), findParams.projectName());
    }

    public GitHubRepository create(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        checkIfGitHubRepositoryExists(gitHubRepositoryAddParams);
        return gitHubRepositoriesRepository.add(gitHubRepositoryAddParams);
    }

    private void checkIfGitHubRepositoryExists(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        getGitHubRepositoryResponse(gitHubRepositoryAddParams.username(), gitHubRepositoryAddParams.name())
                .orElseThrow(() -> new GitHubRepositoryNotFoundException(applicationConfig));
    }

    @Override
    public List<LinkUpdate> getUpdates(List<GitHubRepository> repositories) {
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
}
