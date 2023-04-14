package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.UUID;

public record LinkAddParams(URI url, UUID tgChatId, UUID githubRepositoryId, UUID stackoverflowQuestionId) {
    public LinkAddParams(URI url, TgChat tgChat, GitHubRepository gitHubRepository) {
        this(url, tgChat.id(), gitHubRepository.id(), null);
    }

    public LinkAddParams(URI url, TgChat tgChat, StackOverflowQuestion stackOverflowQuestion) {
        this(url, tgChat.id(), null, stackOverflowQuestion.id());
    }
}
