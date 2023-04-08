package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StackOverflowQuestionsRepository implements BaseRepository<StackOverflowQuestion, StackOverflowQuestionAddParams> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public StackOverflowQuestion add(StackOverflowQuestionAddParams stackOverflowQuestionAddParams) {
        return jdbcTemplate
                .query(
                        "INSERT INTO stackoverflow_questions (question_id) VALUES (?)",
                        resultSetExtractor(),
                        stackOverflowQuestionAddParams.questionId()
                );
    }

    @Override
    public List<StackOverflowQuestion> findAll() {
        return jdbcTemplate.query("SELECT * FROM stackoverflow_questions", rowMapper());
    }

    @Override
    public void remove(UUID id) {
        jdbcTemplate.update("DELETE FROM stackoverflow_questions WHERE id = ?", id);
    }

    private RowMapper<StackOverflowQuestion> rowMapper() {
        return (ResultSet rs, int rowNum) -> resultSetExtractor().extractData(rs);
    }

    private ResultSetExtractor<StackOverflowQuestion> resultSetExtractor() {
        return (ResultSet rs) -> new StackOverflowQuestion(
                rs.getObject("id", UUID.class),
                rs.getLong("question_id"),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }
}
