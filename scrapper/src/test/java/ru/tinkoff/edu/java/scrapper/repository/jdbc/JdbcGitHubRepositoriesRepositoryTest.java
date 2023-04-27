package ru.tinkoff.edu.java.scrapper.repository.jdbc;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.configuration.JdbcConfig;
import ru.tinkoff.edu.java.scrapper.configuration.TestDataSourceConfig;
import ru.tinkoff.edu.java.scrapper.configuration.TransactionConfig;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TransactionConfig.class,
        JdbcGitHubRepositoriesRepository.class,
        TestDataSourceConfig.class,
        JdbcConfig.class
})
@ExtendWith(RandomBeansExtension.class)
public class JdbcGitHubRepositoriesRepositoryTest {
    @Autowired
    JdbcGitHubRepositoriesRepository jdbcGitHubRepositoriesRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Random
    String username;

    @Random
    String name;

    @Random
    OffsetDateTime updatedAt;

    @Test
    @Transactional
    @Rollback
    public void shouldAddGitHubRepository() {
        var addParams = new GitHubRepositoryAddParams(username, name);
        var gitHubRepository = jdbcGitHubRepositoriesRepository.add(addParams);
        assertEquals(addParams.name(), gitHubRepository.name());
        assertEquals(addParams.username(), gitHubRepository.username());

        var sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * from github_repositories");
        assertTrue(sqlRowSet.next());
        assertEquals(addParams.username(), sqlRowSet.getString("username"));
        assertEquals(addParams.name(), sqlRowSet.getString("name"));
        assertFalse(sqlRowSet.next());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllGitHubRepositories() {
        var id = insertGitHubRepository();
        var foundIds = jdbcGitHubRepositoriesRepository.findAll().stream().map(GitHubRepository::id).toList();
        assertIterableEquals(List.of(id), foundIds);
    }
    @Test
    @Transactional
    @Rollback
    public void shouldFindAllGitHubRepositoriesWithLinks() {
        var gitHubRepositoryId = insertGitHubRepository();
        var limit = 100;
        var foundIds = jdbcGitHubRepositoriesRepository
                .findAllWithLinks(limit, JdbcGitHubRepositoriesRepository.UpdateColumn.UPDATED_AT)
                .stream()
                .map(GitHubRepository::id)
                .toList();
        assertIterableEquals(List.of(), foundIds);

        var tgChatId = jdbcTemplate
                .queryForObject("INSERT INTO tg_chats (chat_id) VALUES (1) RETURNING id", UUID.class);
        jdbcTemplate.update(
                "INSERT INTO links (url, tg_chat_id, github_repository_id) VALUES (?, ?, ?)",
                "test",
                tgChatId,
                gitHubRepositoryId
        );
        foundIds = jdbcGitHubRepositoriesRepository
                .findAllWithLinks(limit, JdbcGitHubRepositoriesRepository.UpdateColumn.UPDATED_AT)
                .stream()
                .map(GitHubRepository::id).toList();
        assertIterableEquals(List.of(gitHubRepositoryId), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllGitHubRepository() {
        var id = insertGitHubRepository();
        var foundId = jdbcGitHubRepositoriesRepository.find(username, name).map(GitHubRepository::id);
        assertEquals(Optional.of(id), foundId);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveGitHubRepository() {
        var id = insertGitHubRepository();
        jdbcGitHubRepositoriesRepository.remove(id);

        var ids = jdbcTemplate
                .queryForList("SELECT id from github_repositories where id = ?", UUID.class, id);
        assertTrue(ids.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdateUpdatedAt() {
        var id = insertGitHubRepository();
        updatedAt = updatedAt.withNano(0);
        GitHubRepository repository = when(mock(GitHubRepository.class).id()).thenReturn(id).getMock();
        jdbcGitHubRepositoriesRepository.updateUpdatedAt(List.of(repository), updatedAt);
        var result = jdbcTemplate
                .queryForObject("SELECT updated_at from github_repositories", OffsetDateTime.class);
        assertNotNull(result);
        assertTrue(updatedAt.isEqual(result));
    }

    private UUID insertGitHubRepository() {
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO github_repositories (name, username)
                        VALUES (?, ?) RETURNING id
                        """,
                UUID.class,
                name,
                username
        );
    }
}
