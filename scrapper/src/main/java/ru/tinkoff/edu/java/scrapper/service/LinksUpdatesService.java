package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;

import java.time.OffsetDateTime;
import java.util.List;

public interface LinksUpdatesService<T> {
    List<LinkUpdate> getLinkUpdates(List<T> objects);
    void updateUpdatedAt(List<T> objects, OffsetDateTime updatedAt);
    List<T> getForLinksUpdate(int first);
}
