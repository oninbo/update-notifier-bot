package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import ru.tinkoff.edu.java.scrapper.dto.GitHubIssueResponse;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryResponse;

import java.util.List;

public interface GitHubClient {
    @GetExchange("/repos/{owner}/{repo}")
    GitHubRepositoryResponse getRepository(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(name = "X-GitHub-Api-Version") String version
    );

    @GetExchange("/repos/{owner}/{repo}/issues")
    List<GitHubIssueResponse> getRepositoryIssues(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(name = "X-GitHub-Api-Version") String version,
            @RequestParam String since,
            @RequestParam int page,
            @RequestParam(name = "per_page") int perPage
    );
}
