package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.OffsetDateTime;

public record GitHubIssueResponse(
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("html_url") URI url,
        @JsonProperty("pull_request") PullRequest pullRequest
) {
    public record PullRequest() {
    }
}
