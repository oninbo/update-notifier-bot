package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.LinkAddParams;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.util.UUID;

public interface JpaLinksRepository extends JpaRepository<LinkEntity, UUID> {
    default Link add(LinkAddParams linkAddParams) {
        var entity = new LinkEntity();
        entity.setUrl(linkAddParams.url());
        entity.setTgChat(new TgChatEntity(linkAddParams.tgChatId()));
        entity.setGitHubRepository(new GitHubRepositoryEntity(linkAddParams.githubRepositoryId()));
        entity.setStackOverflowQuestion(new StackOverflowQuestionEntity(linkAddParams.stackoverflowQuestionId()));
        save(entity);
        return new Link(entity.getId(), entity.getUrl());
    }

    default void remove(UUID id) {
        findById(id).ifPresent(this::delete);
    }
}
