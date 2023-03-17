package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;

public interface GitHubClient {
    @GetExchange("/repos/{owner}/{repo}")
    GitHubRepository getRepository(@PathVariable String owner, @PathVariable String repo);
}
