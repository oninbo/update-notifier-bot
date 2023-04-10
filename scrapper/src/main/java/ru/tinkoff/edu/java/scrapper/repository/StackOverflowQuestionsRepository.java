package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StackOverflowQuestionsRepository implements BaseRepository<StackOverflowQuestion, StackOverflowQuestionAddParams> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public StackOverflowQuestion add(StackOverflowQuestionAddParams stackOverflowQuestionAddParams) {
        return jdbcTemplate
                .query(
                        "INSERT INTO stackoverflow_questions (question_id) VALUES (?) RETURNING *",
                        rs -> {
                            rs.next();
                            return extractStackOverflowQuestion(rs);
                        },
                        stackOverflowQuestionAddParams.questionId()
                );
    }

    public StackOverflowQuestion add(Long questionId) {
        return add(new StackOverflowQuestionAddParams(questionId));
    }

    @Override
    public List<StackOverflowQuestion> findAll() {
        return jdbcTemplate.query("SELECT * FROM stackoverflow_questions", rowMapper());
    }

    public List<StackOverflowQuestion> findAllWithLinks() {
        return jdbcTemplate.query("""
                        SELECT sq.*
                        FROM links
                        JOIN stackoverflow_questions sq on sq.id = links.stackoverflow_question_id
                        GROUP BY sq.id, sq.question_id, sq.created_at, sq.updated_at
                        """,
                rowMapper()
        );
    }

    public Optional<StackOverflowQuestion> find(Link link) {
        var result = jdbcTemplate.query(
                """
                SELECT stackoverflow_questions.*
                FROM stackoverflow_questions
                JOIN links l on stackoverflow_questions.id = l.stackoverflow_question_id
                WHERE l.id = ?
                """,
                rowMapper(),
                link.id()
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<StackOverflowQuestion> find(Long questionId) {
        var result = jdbcTemplate.query(
                "SELECT * FROM stackoverflow_questions WHERE question_id = ?",
                rowMapper(),
                questionId
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void update(StackOverflowQuestion stackOverflowQuestion, OffsetDateTime updatedAt) {
        var id = stackOverflowQuestion.id();
        jdbcTemplate.update("UPDATE stackoverflow_questions SET updated_at = ? WHERE id = ?", updatedAt, id);
    }

    @Override
    public void remove(UUID id) {
        jdbcTemplate.update("DELETE FROM stackoverflow_questions WHERE id = ?", id);
    }

    private RowMapper<StackOverflowQuestion> rowMapper() {
        return (ResultSet rs, int rowNum) -> extractStackOverflowQuestion(rs);
    }

    private StackOverflowQuestion extractStackOverflowQuestion(ResultSet rs) throws SQLException {
        return new StackOverflowQuestion(
                rs.getObject("id", UUID.class),
                rs.getLong("question_id"),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }
}
