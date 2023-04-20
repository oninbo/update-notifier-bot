package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("JpaQlInspection") // Ругается на вызов конструктора DTO в запросах
public interface JpaLinksRepository extends JpaRepository<LinkEntity, UUID> {
    default Link add(URI url, TgChatEntity tgChat, GitHubRepositoryEntity gitHubRepository) {
        var entity = new LinkEntity();
        entity.setUrl(url);
        entity.setTgChat(tgChat);
        entity.setGitHubRepository(gitHubRepository);
        save(entity);
        return new Link(entity.getId(), entity.getUrl());
    }

    @Query("SELECT new ru.tinkoff.edu.java.scrapper.dto.Link(l.id, l.url) FROM LinkEntity AS l")
    List<Link> findAllLinks();

    @Query("""
                SELECT new ru.tinkoff.edu.java.scrapper.dto.Link(l.id, l.url)
                FROM LinkEntity AS l
                WHERE l.gitHubRepository.id = :#{#gitHubRepository.id} AND l.tgChat.id = :#{#tgChat.id}
            """)
    Optional<Link> find(@Param("tgChat") TgChat tgChat, @Param("gitHubRepository") GitHubRepository gitHubRepository);

    @Query("""
                SELECT new ru.tinkoff.edu.java.scrapper.dto.Link(l.id, l.url)
                FROM LinkEntity AS l
                WHERE l.stackOverflowQuestion.id = :#{#stackOverflowQuestion.id} AND l.tgChat.id = :#{#tgChat.id}
            """)
    Optional<Link> find(@Param("tgChat") TgChat tgChat, @Param("stackOverflowQuestion") StackOverflowQuestion stackOverflowQuestion);

    @Query("""
                SELECT new ru.tinkoff.edu.java.scrapper.dto.Link(l.id, l.url)
                FROM LinkEntity AS l
                WHERE l.tgChat.id = :#{#tgChat.id}
            """)
    List<Link> findAll(@Param("tgChat") TgChat tgChat);
}
