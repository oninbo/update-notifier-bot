package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public record GitHubIssueResponse(
        @JsonProperty("html_url") URI url,
        @JsonProperty("pull_request") PullRequest pullRequest
) {
    public record PullRequest() {
    }
}
