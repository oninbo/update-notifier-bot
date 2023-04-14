package ru.tinkoff.edu.java.scrapper.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
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
public class JdbcStackOverflowQuestionsRepository implements BaseRepository<StackOverflowQuestion, StackOverflowQuestionAddParams> {
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

    public enum UpdateColumn {
        UPDATED_AT,
        ANSWERS_UPDATED_AT
    }

    public List<StackOverflowQuestion> findAllWithLinks(int limit, UpdateColumn updateColumn) {
        String sql = String.format(
                """
                        SELECT sq.*
                        FROM links
                        JOIN stackoverflow_questions sq on sq.id = links.stackoverflow_question_id
                        GROUP BY sq.id, sq.question_id, sq.created_at, sq.updated_at
                        ORDER BY sq.%s NULLS FIRST
                        LIMIT ?
                        """,
                updateColumn.name().toLowerCase()
        );
        return jdbcTemplate.query(sql, rowMapper(), limit);
    }

    public Optional<StackOverflowQuestion> find(Long questionId) {
        var result = jdbcTemplate.query(
                "SELECT * FROM stackoverflow_questions WHERE question_id = ?",
                rowMapper(),
                questionId
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void updateUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        jdbcTemplate.batchUpdate(
                "UPDATE stackoverflow_questions SET updated_at = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {

                    public void setValues(@NotNull PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setObject(1, updatedAt);
                        ps.setObject(2, questions.get(i).id());
                    }

                    public int getBatchSize() {
                        return questions.size();
                    }
                }
        );
    }

    public void updateAnswersUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        jdbcTemplate.batchUpdate(
                "UPDATE stackoverflow_questions SET answers_updated_at = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {

                    public void setValues(@NotNull PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setObject(1, updatedAt);
                        ps.setObject(2, questions.get(i).id());
                    }

                    public int getBatchSize() {
                        return questions.size();
                    }
                }
        );
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
                rs.getObject("updated_at", OffsetDateTime.class),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("answers_updated_at", OffsetDateTime.class)
        );
    }
}
