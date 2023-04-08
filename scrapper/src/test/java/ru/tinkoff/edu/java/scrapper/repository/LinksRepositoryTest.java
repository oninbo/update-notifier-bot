package ru.tinkoff.edu.java.scrapper.repository;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.configuration.TransactionConfiguration;
import ru.tinkoff.edu.java.scrapper.dto.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TransactionConfiguration.class, LinksRepository.class})
@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
public class LinksRepositoryTest {
    @Autowired
    LinksRepository linksRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Mock
    TgChat tgChat;

    @Mock
    GitHubRepository gitHubRepository;

    @Random
    URI url;

    @BeforeEach
    public void setUp() {
        var tgChatId = jdbcTemplate.queryForObject(
                "INSERT INTO tg_chats (chat_id) VALUES (1) RETURNING id",
                UUID.class
        );
        when(tgChat.id()).thenReturn(tgChatId);
        var gitHubRepositoryId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO github_repositories (username, name)
                        VALUES ('test_user', 'test_project') RETURNING id
                        """,
                UUID.class
        );
        when(gitHubRepository.id()).thenReturn(gitHubRepositoryId);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldAddLink() {
        var link = linksRepository.add(new LinkAddParams(url, tgChat, gitHubRepository));
        assertEquals(url, link.url());

        var linkTgChatId = jdbcTemplate.queryForObject("SELECT tg_chat_id from links", UUID.class);
        assertEquals(tgChat.id(), linkTgChatId);

        var gitHubRepositoryId =
                jdbcTemplate.queryForObject("SELECT github_repository_id from links", UUID.class);
        assertEquals(gitHubRepository.id(), gitHubRepositoryId);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllLinks() {
        var linkId = insertLink();
        var foundLinks = linksRepository.findAll();
        assertIterableEquals(List.of(new Link(linkId, url)), foundLinks);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveLink() {
        var id = insertLink();
        linksRepository.remove(id);

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
}
