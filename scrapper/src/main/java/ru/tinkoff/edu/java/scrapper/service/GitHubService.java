package ru.tinkoff.edu.java.scrapper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryResponse;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final GitHubClient gitHubClient;
    private final ApplicationConfig applicationConfig;

    public GitHubRepositoryResponse getGitHubRepositoryFromApi(String ownerUserName, String projectName) {
        return gitHubClient.getRepository(ownerUserName, projectName, gitHubApiVersion());
    }

    private String gitHubApiVersion() {
        return applicationConfig.webClient().github().apiVersion();
    }
}
