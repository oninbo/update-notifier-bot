package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.LinkAddParams;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;

import java.util.UUID;

public interface JpaLinksRepository extends JpaRepository<LinkEntity, UUID> {
    default Link add(LinkAddParams linkAddParams) {
        return new Link(UUID.randomUUID(), linkAddParams.url());
    }

    default void remove(UUID id) {
    }
}
