package ru.tinkoff.edu.java.scrapper.repository.jpa;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
public class JpaGitHubRepositoriesRepositoryTest extends JpaRepositoryTest {
    @Autowired
    JpaGitHubRepositoriesRepository jpaGitHubRepositoriesRepository;

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
        var gitHubRepository = jpaGitHubRepositoriesRepository.add(addParams);
        assertEquals(addParams.name(), gitHubRepository.getName());
        assertEquals(addParams.username(), gitHubRepository.getUsername());

        entityManager.flush();
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
        var foundIds =
                jpaGitHubRepositoriesRepository.findAll().stream().map(GitHubRepositoryEntity::getId).toList();
        assertIterableEquals(List.of(id), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllGitHubRepositoriesWithLinks() {
        var gitHubRepositoryId = insertGitHubRepository();
        var limit = 100;
        Supplier<List<UUID>> findIds = () -> jpaGitHubRepositoriesRepository
                .findAllWithLinks(limit, JpaGitHubRepositoriesRepository.OrderColumn.updatedAt)
                .stream()
                .map(GitHubRepositoryEntity::getId)
                .toList();
        var foundIds = findIds.get();
        assertIterableEquals(List.of(), foundIds);

        var tgChatId = jdbcTemplate
                .queryForObject("INSERT INTO tg_chats (chat_id) VALUES (1) RETURNING id", UUID.class);
        jdbcTemplate.update(
                "INSERT INTO links (url, tg_chat_id, github_repository_id) VALUES (?, ?, ?)",
                "test",
                tgChatId,
                gitHubRepositoryId
        );
        foundIds = findIds.get();
        assertIterableEquals(List.of(gitHubRepositoryId), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllGitHubRepository() {
        var id = insertGitHubRepository();
        var foundId = jpaGitHubRepositoriesRepository
                .findByUsernameAndName(username, name).map(GitHubRepositoryEntity::getId);
        assertEquals(Optional.of(id), foundId);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveGitHubRepository() {
        var id = insertGitHubRepository();
        jpaGitHubRepositoriesRepository.deleteById(id);
        entityManager.flush();

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
        jpaGitHubRepositoriesRepository.findById(id).ifPresent(
                entity -> {
                    entity.setUpdatedAt(updatedAt);
                    jpaGitHubRepositoriesRepository.saveAndFlush(entity);
                }
        );
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
