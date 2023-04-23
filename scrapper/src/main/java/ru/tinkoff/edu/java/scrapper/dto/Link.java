package ru.tinkoff.edu.java.scrapper.dto;

import java.net.URI;
import java.util.UUID;

public record Link(UUID id, URI url) {
}
