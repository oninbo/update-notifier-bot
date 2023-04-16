package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.*;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LinksRepository implements BaseRepository<Link, LinkAddParams> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Link add(LinkAddParams linkAddParams) {
        String sql = """
                INSERT INTO links (url, tg_chat_id, github_repository_id, stackoverflow_question_id)
                VALUES (?, ?, ?, ?) RETURNING *
                """;
        return jdbcTemplate
                .query(
                        sql,
                        rs -> {
                            rs.next();
                            return extractLink(rs);
                        },
                        linkAddParams.url().toString(),
                        linkAddParams.tgChatId(),
                        linkAddParams.githubRepositoryId(),
                        linkAddParams.stackoverflowQuestionId()
                );
    }

    public Optional<Link> find(TgChat tgChat, GitHubRepository gitHubRepository) {
        var result = jdbcTemplate
                .query(
                        "SELECT * FROM links WHERE tg_chat_id = ? AND github_repository_id = ?",
                        rowMapper(),
                        tgChat.id(),
                        gitHubRepository.id()
                );
        return result.isEmpty() ? Optional.empty() :  Optional.of(result.get(0));
    }

    public Optional<Link> find(TgChat tgChat, StackOverflowQuestion stackOverflowQuestion) {
        var result = jdbcTemplate
                .query(
                        "SELECT * FROM links WHERE tg_chat_id = ? AND stackoverflow_question_id = ?",
                        rowMapper(),
                        tgChat.id(),
                        stackOverflowQuestion.id()
                );
        return result.isEmpty() ? Optional.empty() :  Optional.of(result.get(0));
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM links", rowMapper());
    }

    public List<Link> findAll(TgChat tgChat) {
        return jdbcTemplate.query("SELECT * FROM links WHERE tg_chat_id = ?", rowMapper(), tgChat.id());
    }

    public List<LinkWithChatId> findAllWithChatId(GitHubRepository gitHubRepository, OffsetDateTime createdBefore) {
        return jdbcTemplate.query(
                """
                SELECT links.*, tc.chat_id
                FROM links
                JOIN tg_chats tc on tc.id = links.tg_chat_id
                WHERE github_repository_id = ? AND links.created_at < ?
                """,
                (rs, n) -> new LinkWithChatId(
                        rs.getObject("id", UUID.class),
                        URI.create(rs.getString("url")),
                        rs.getLong("chat_id")
                ),
                gitHubRepository.id(),
                createdBefore
        );
    }

    public List<LinkWithChatId> findAllWithChatId(StackOverflowQuestion question, OffsetDateTime createdBefore) {
        return jdbcTemplate.query(
                """
                SELECT links.*, tc.chat_id
                FROM links
                JOIN tg_chats tc on tc.id = links.tg_chat_id
                WHERE stackoverflow_question_id = ? AND links.created_at < ?
                """,
                (rs, n) -> new LinkWithChatId(
                        rs.getObject("id", UUID.class),
                        URI.create(rs.getString("url")),
                        rs.getLong("chat_id")
                ),
                question.id(),
                createdBefore
        );
    }

    @Override
    public void remove(UUID id) {
        jdbcTemplate.update("DELETE FROM links WHERE id = ?", id);
    }

    private RowMapper<Link> rowMapper() {
        return (ResultSet rs, int rowNum) -> extractLink(rs);
    }

    private Link extractLink(ResultSet rs) throws SQLException {
        return new Link(
                rs.getObject("id", UUID.class),
                URI.create(rs.getString("url"))
        );
    }
}
