package ru.tinkoff.edu.java.bot.dto;

import java.net.URI;

public record LinkResponse(
        long id,
        URI url
) {
}
