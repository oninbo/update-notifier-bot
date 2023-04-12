package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public record GitHubIssueResponse(URI url, @JsonProperty("repository_url") URI repositoryUrl) {
}
