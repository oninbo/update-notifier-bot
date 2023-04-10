package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
                        rs -> {
                            rs.next();
                            return extractGitHubRepository(rs);
                        },
                        gitHubRepositoryAddParams.username(),
                        gitHubRepositoryAddParams.name()
                );
    }

    public void update(UUID id, OffsetDateTime updatedAt) {
        jdbcTemplate.update("UPDATE github_repositories SET updated_at = ? WHERE id = ?", updatedAt, id);
    }

    public Optional<GitHubRepository> find(String username, String name) {
        var result = jdbcTemplate.query(
                "SELECT * FROM github_repositories WHERE name = ? AND username = ?",
                rowMapper(),
                name,
                username
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<GitHubRepository> findAll() {
        return jdbcTemplate.query("SELECT * FROM github_repositories", rowMapper());
    }

    public List<GitHubRepository> findAllWithLinks() {
        return jdbcTemplate.query("""
                        SELECT gr.*
                        FROM links
                        JOIN github_repositories gr on links.github_repository_id = gr.id
                        GROUP BY gr.id, gr.username, gr.name, gr.created_at, gr.updated_at
                        """,
                rowMapper()
        );
    }

    @Override
    public void remove(UUID id) {
        jdbcTemplate.update("DELETE FROM github_repositories WHERE id = ?", id);
    }

    private RowMapper<GitHubRepository> rowMapper() {
        return (ResultSet rs, int rowNum) -> extractGitHubRepository(rs);
    }

    private GitHubRepository extractGitHubRepository(ResultSet rs) throws SQLException {
        return new GitHubRepository(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("name"),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }
}
