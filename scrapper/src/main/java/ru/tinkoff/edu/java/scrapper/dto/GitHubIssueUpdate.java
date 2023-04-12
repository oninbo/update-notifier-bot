package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.List;

public record GitHubIssueUpdate(
        URI issueUrl,
        GitHubRepository repository,
        List<Long> chatIds) {
    public record GitHubRepository(URI url, String name, String username){
    }
}
