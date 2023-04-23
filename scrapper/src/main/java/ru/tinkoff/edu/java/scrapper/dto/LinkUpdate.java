package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public record LinkUpdate(
        UUID id,
        URI url,
        List<Long> tgChatIds
) {
}
