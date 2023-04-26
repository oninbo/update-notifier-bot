package ru.tinkoff.edu.java.scrapper.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcGitHubRepositoriesRepository implements BaseRepository<GitHubRepository, GitHubRepositoryAddParams> {
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

    public enum UpdateColumn {
        UPDATED_AT,
        ISSUES_UPDATED_AT,
    }

    public List<GitHubRepository> findAllWithLinks(int limit, UpdateColumn updateColumn) {
        String sql = String.format(
                """
                        SELECT gr.*
                        FROM links
                        JOIN github_repositories gr on links.github_repository_id = gr.id
                        GROUP BY gr.id, gr.username, gr.name, gr.created_at, gr.updated_at
                        ORDER BY gr.%s NULLS FIRST
                        LIMIT ?
                        """,
                updateColumn.name().toLowerCase()
        );
        return jdbcTemplate.query(sql, rowMapper(), limit);
    }

    public void updateUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt) {
        jdbcTemplate.batchUpdate(
                "UPDATE github_repositories SET updated_at = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {

                    public void setValues(@NotNull PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setObject(1, updatedAt);
                        ps.setObject(2, repositories.get(i).id());
                    }

                    public int getBatchSize() {
                        return repositories.size();
                    }
                }
        );
    }

    public void updateIssuesUpdatedAt(List<GitHubRepository> repositories, OffsetDateTime updatedAt) {
        jdbcTemplate.batchUpdate(
                "UPDATE github_repositories SET issues_updated_at = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {

                    public void setValues(@NotNull PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setObject(1, updatedAt);
                        ps.setObject(2, repositories.get(i).id());
                    }

                    public int getBatchSize() {
                        return repositories.size();
                    }
                }
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
                rs.getObject("updated_at", OffsetDateTime.class),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("issues_updated_at", OffsetDateTime.class)
        );
    }
}
