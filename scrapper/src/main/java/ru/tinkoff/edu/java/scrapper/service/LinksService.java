package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.Link;

import java.net.URI;
import java.util.List;

public interface LinksService {
    List<Link> getLinks(Long chatId);

    Link addLink(Long chatId, URI url);

    Link deleteLink(Long chatId, URI url);
}
