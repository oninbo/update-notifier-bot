package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryResponse;

public interface GitHubClient {
    @GetExchange("/repos/{owner}/{repo}")
    GitHubRepositoryResponse getRepository(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(name = "X-GitHub-Api-Version") String version
    );
}
