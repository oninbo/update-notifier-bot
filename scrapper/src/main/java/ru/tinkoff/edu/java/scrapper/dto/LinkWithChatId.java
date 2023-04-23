package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.UUID;

public record LinkWithChatId(UUID id, URI url, Long chatId) {
}
