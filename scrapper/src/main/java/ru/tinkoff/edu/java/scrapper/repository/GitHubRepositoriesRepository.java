package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GitHubRepositoriesRepository implements BaseRepository<GitHubRepository, GitHubRepositoryAddParams> {
    public final JdbcTemplate jdbcTemplate;

    @Override
    public GitHubRepository add(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        return jdbcTemplate
                .query(
                        "INSERT INTO github_repositories (username, name) VALUES (?, ?) RETURNING *",
                        resultSetExtractor(),
                        gitHubRepositoryAddParams.username(),
                        gitHubRepositoryAddParams.name()
                );
    }

    @Override
    public List<GitHubRepository> findAll() {
        return jdbcTemplate.query("SELECT * FROM github_repositories", rowMapper());
    }

    @Override
    public void remove(UUID id) {
        jdbcTemplate.update("DELETE FROM github_repositories WHERE id = ?", id);
    }

    private RowMapper<GitHubRepository> rowMapper() {
        return (ResultSet rs, int rowNum) -> resultSetExtractor().extractData(rs);
    }

    private ResultSetExtractor<GitHubRepository> resultSetExtractor() {
        return (ResultSet rs) -> new GitHubRepository(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("name"),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }
}
