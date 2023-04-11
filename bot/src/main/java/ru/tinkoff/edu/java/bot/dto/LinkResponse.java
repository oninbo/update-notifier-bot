package ru.tinkoff.edu.java.bot.dto;

import java.net.URI;
import java.util.UUID;

public record LinkResponse(
        UUID id,
        URI url
) {
}
