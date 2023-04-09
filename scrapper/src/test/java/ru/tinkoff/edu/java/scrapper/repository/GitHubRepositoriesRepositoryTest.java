package ru.tinkoff.edu.java.scrapper.repository;

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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TransactionConfig.class,
        GitHubRepositoriesRepository.class,
        TestDataSourceConfig.class,
        JdbcConfig.class
})
@ExtendWith(RandomBeansExtension.class)
public class GitHubRepositoriesRepositoryTest {
    @Autowired
    GitHubRepositoriesRepository gitHubRepositoriesRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Random
    String username;

    @Random
    String name;

    @Test
    @Transactional
    @Rollback
    public void shouldAddGitHubRepository() {
        var addParams = new GitHubRepositoryAddParams(username, name);
        var gitHubRepository = gitHubRepositoriesRepository.add(addParams);
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
        var foundIds = gitHubRepositoriesRepository.findAll().stream().map(GitHubRepository::id).toList();
        assertIterableEquals(List.of(id), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveGitHubRepository() {
        var id = insertGitHubRepository();
        gitHubRepositoriesRepository.remove(id);

        var ids = jdbcTemplate
                .queryForList("SELECT id from github_repositories where id = ?", UUID.class, id);
        assertTrue(ids.isEmpty());
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
