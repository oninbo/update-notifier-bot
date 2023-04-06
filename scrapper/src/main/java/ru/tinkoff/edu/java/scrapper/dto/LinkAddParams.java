package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.UUID;

public record LinkAddParams(URI url, UUID tgChatId, UUID githubRepositoryId, UUID stackoverflowId) {
}
