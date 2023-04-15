package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.GitHubIssueUpdate;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface GitHubIssuesService extends LinksUpdatesService<GitHubRepository> {
    List<GitHubIssueUpdate> getGitHubIssueUpdates(List<GitHubRepository> repositories);
    void updateIssuesUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt);
    List<GitHubRepository> getForIssuesUpdate(int first);
}
