package ru.tinkoff.edu.java.scrapper.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.WebClient;
import ru.tinkoff.edu.java.scrapper.configuration.WebClientSettings;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryResponse;
import ru.tinkoff.edu.java.scrapper.dto.GitHubUserResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitHubServiceTest {
    public GitHubService gitHubService;
    GitHubRepositoryResponse mockRepository;

    @BeforeEach
    void initialize() {
        GitHubUserResponse mockUser = new GitHubUserResponse("mock_user", 1L);
        mockRepository = new GitHubRepositoryResponse("mock_project", mockUser);
        GitHubClient gitHubClient = (owner, repo, version) -> mockRepository;
        WebClient webClient = new WebClient(new WebClientSettings(null, "1"), null);
        ApplicationConfig applicationConfig = new ApplicationConfig(null, webClient);
        gitHubService = new GitHubService(gitHubClient, applicationConfig);
    }

    @Test
    public void shouldGetGitHubRepository() {
        var result = gitHubService.getGitHubRepositoryFromApi(mockRepository.owner().login(), mockRepository.name());
        assertEquals(result, mockRepository);
    }
}
