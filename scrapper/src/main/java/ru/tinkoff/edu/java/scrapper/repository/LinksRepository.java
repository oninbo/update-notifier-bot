package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.LinkAddParams;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
                        linkAddParams.stackoverflowId()
                );
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM links", rowMapper());
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
