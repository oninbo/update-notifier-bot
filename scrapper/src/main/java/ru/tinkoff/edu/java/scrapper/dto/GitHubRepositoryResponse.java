package ru.tinkoff.edu.java.scrapper.dto;

public record GitHubRepositoryResponse(String name, GitHubUserResponse owner) {
}
