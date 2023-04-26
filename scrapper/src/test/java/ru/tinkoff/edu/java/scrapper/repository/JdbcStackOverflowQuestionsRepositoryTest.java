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
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcStackOverflowQuestionsRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TransactionConfig.class,
        JdbcStackOverflowQuestionsRepository.class,
        TestDataSourceConfig.class,
        JdbcConfig.class
})
@ExtendWith(RandomBeansExtension.class)
public class JdbcStackOverflowQuestionsRepositoryTest {
    @Autowired
    JdbcStackOverflowQuestionsRepository jdbcStackOverflowQuestionsRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Random
    Long questionId;

    @Random
    OffsetDateTime updatedAt;

    @Test
    @Transactional
    @Rollback
    public void shouldAddStackOverflowQuestion() {
        var addParams = new StackOverflowQuestionAddParams(questionId);
        var stackOverflowQuestion = jdbcStackOverflowQuestionsRepository.add(addParams);
        assertEquals(questionId, stackOverflowQuestion.questionId());

        var sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * from stackoverflow_questions");
        assertTrue(sqlRowSet.next());
        assertEquals(questionId, sqlRowSet.getLong("question_id"));
        assertFalse(sqlRowSet.next());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllStackOverflowQuestions() {
        var id = insertStackOverflowQuestion();
        var foundIds = jdbcStackOverflowQuestionsRepository
                .findAll()
                .stream()
                .map(StackOverflowQuestion::id)
                .toList();
        assertIterableEquals(List.of(id), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveStackOverflowQuestion() {
        var id = insertStackOverflowQuestion();
        jdbcStackOverflowQuestionsRepository.remove(id);

        var ids = jdbcTemplate
                .queryForList("SELECT id from stackoverflow_questions where id = ?", UUID.class, id);
        assertTrue(ids.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdateUpdatedAt() {
        var id = insertStackOverflowQuestion();
        updatedAt = updatedAt.withNano(0);
        StackOverflowQuestion question = when(mock(StackOverflowQuestion.class).id()).thenReturn(id).getMock();
        jdbcStackOverflowQuestionsRepository.updateUpdatedAt(List.of(question), updatedAt);
        var result = jdbcTemplate
                .queryForObject("SELECT updated_at from stackoverflow_questions", OffsetDateTime.class);
        assertNotNull(result);
        assertTrue(updatedAt.isEqual(result));
    }

    private UUID insertStackOverflowQuestion() {
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO stackoverflow_questions (question_id)
                        VALUES (?) RETURNING id
                        """,
                UUID.class,
                questionId
        );
    }
}
