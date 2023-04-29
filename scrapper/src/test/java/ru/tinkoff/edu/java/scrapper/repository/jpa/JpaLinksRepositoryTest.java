package ru.tinkoff.edu.java.scrapper.repository.jpa;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
public class JpaLinksRepositoryTest extends JpaRepositoryTest {
    @Autowired
    JpaLinksRepository jpaLinksRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Mock
    TgChat tgChat;

    @Mock
    GitHubRepository gitHubRepository;

    @Mock
    StackOverflowQuestion stackOverflowQuestion;

    @Random
    URI url;

    @BeforeEach
    public void setUp() {
        var tgChatId = jdbcTemplate.queryForObject(
                "INSERT INTO tg_chats (chat_id) VALUES (1) RETURNING id",
                UUID.class
        );
        when(tgChat.id()).thenReturn(tgChatId);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldAddLink() {
        insertGitHubRepository();
        TgChatEntity tgChatEntity = entityManager.find(TgChatEntity.class, tgChat.id());
        GitHubRepositoryEntity gitHubRepositoryEntity =
                entityManager.find(GitHubRepositoryEntity.class, gitHubRepository.id());
        var link = jpaLinksRepository.add(url, tgChatEntity, gitHubRepositoryEntity);
        assertEquals(url, link.url());
        entityManager.flush();
        var sqlRowSet = jdbcTemplate
                .queryForRowSet("SELECT tg_chat_id, github_repository_id from links");
        assertTrue(sqlRowSet.next());
        assertEquals(tgChat.id().toString(), sqlRowSet.getString("tg_chat_id"));
        assertEquals(gitHubRepository.id().toString(), sqlRowSet.getString("github_repository_id"));
        assertFalse(sqlRowSet.next());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllLinks() {
        insertGitHubRepository();
        var linkId = insertLink();
        var foundIds = jpaLinksRepository.findAll().stream().map(LinkEntity::getId).toList();
        assertIterableEquals(List.of(linkId), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllLinksByTgChat() {
        insertGitHubRepository();
        var linkId = insertLink();
        var foundLinks = jpaLinksRepository.findAllByChatId(tgChat.chatId());
        assertIterableEquals(List.of(new Link(linkId, url)), foundLinks);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindLinkWithGitHubRepository() {
        insertGitHubRepository();
        var linkId = insertLink();
        var foundLink = jpaLinksRepository.find(tgChat, gitHubRepository);
        assertTrue(foundLink.isPresent());
        assertEquals(new Link(linkId, url), foundLink.get());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindLinkWithStackOverflowQuestion() {
        var stackOverflowQuestionId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO stackoverflow_questions (question_id)
                        VALUES (1) RETURNING id
                        """,
                UUID.class
        );
        when(stackOverflowQuestion.id()).thenReturn(stackOverflowQuestionId);

        var linkId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO links (url, tg_chat_id, stackoverflow_question_id)
                        VALUES (?, ?, ?) RETURNING id
                        """,
                UUID.class,
                url.toString(),
                tgChat.id(),
                stackOverflowQuestion.id()
        );

        var foundLink = jpaLinksRepository.find(tgChat, stackOverflowQuestion);
        assertTrue(foundLink.isPresent());
        assertEquals(new Link(linkId, url), foundLink.get());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveLink() {
        insertGitHubRepository();
        var id = insertLink();
        jpaLinksRepository.deleteById(id);
        entityManager.flush();

        var ids = jdbcTemplate.queryForList("SELECT id from links where id = ?", UUID.class, id);
        assertTrue(ids.isEmpty());
    }

    private UUID insertLink() {
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO links (url, tg_chat_id, github_repository_id)
                        VALUES (?, ?, ?) RETURNING id
                        """,
                UUID.class,
                url.toString(),
                tgChat.id(),
                gitHubRepository.id()
        );
    }

    private void insertGitHubRepository() {
        var gitHubRepositoryId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO github_repositories (username, name)
                        VALUES ('test_user', 'test_project') RETURNING id
                        """,
                UUID.class
        );
        when(gitHubRepository.id()).thenReturn(gitHubRepositoryId);
    }
}
