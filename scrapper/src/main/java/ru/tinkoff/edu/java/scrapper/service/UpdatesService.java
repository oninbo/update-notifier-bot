package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;

import java.util.List;

public interface UpdatesService {
    List<LinkUpdate> getUpdates();
}
