package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.GitHubIssueUpdate;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;

import java.util.List;

public interface GitHubIssuesService extends UpdatesService<GitHubRepository> {
    List<GitHubIssueUpdate> getGitHubIssueUpdates(List<GitHubRepository> repositories);
}
