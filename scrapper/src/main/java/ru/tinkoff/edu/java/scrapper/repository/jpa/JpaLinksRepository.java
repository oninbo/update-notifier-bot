package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.net.URI;
import java.time.OffsetDateTime;
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

    default Link add(URI url, TgChatEntity tgChat, StackOverflowQuestionEntity stackOverflowQuestion) {
        var entity = new LinkEntity();
        entity.setUrl(url);
        entity.setTgChat(tgChat);
        entity.setStackOverflowQuestion(stackOverflowQuestion);
        save(entity);
        return new Link(entity.getId(), entity.getUrl());
    }

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
            SELECT new ru.tinkoff.edu.java.scrapper.dto.LinkWithChatId(l.id, l.url, tc.chatId)
            FROM LinkEntity AS l
            JOIN TgChatEntity AS tc ON tc.id = l.tgChat.id
            WHERE l.gitHubRepository.id = :#{#gitHubRepository.id} AND l.createdAt < :createdBefore
            """)
    List<LinkWithChatId> findAllWithChatId(
            @Param("gitHubRepository") GitHubRepository gitHubRepository,
            @Param("createdBefore") OffsetDateTime createdBefore
    );

    @Query("""
            SELECT new ru.tinkoff.edu.java.scrapper.dto.LinkWithChatId(l.id, l.url, tc.chatId)
            FROM LinkEntity AS l
            JOIN TgChatEntity AS tc ON tc.id = l.tgChat.id
            WHERE l.stackOverflowQuestion.id = :#{#stackOverflowQuestion.id} AND l.createdAt < :createdBefore
            """)
    List<LinkWithChatId> findAllWithChatId(
            @Param("stackOverflowQuestion") StackOverflowQuestion question,
            @Param("createdBefore") OffsetDateTime createdBefore
    );

    @Query("SELECT l FROM LinkEntity AS l JOIN TgChatEntity AS c ON c.id = l.tgChat.id WHERE c.chatId = :chatId")
    List<LinkEntity> findAllByChatId(@Param("chatId") Long chatId);

    Optional<LinkEntity> findByTgChatAndGitHubRepository(TgChatEntity tgChat,
                                                         GitHubRepositoryEntity gitHubRepository);

    Optional<LinkEntity> findByTgChatAndStackOverflowQuestion(TgChatEntity tgChat,
                                                              StackOverflowQuestionEntity stackOverflowQuestion);
}
