package ru.tinkoff.edu.java.scrapper.repository.jpa;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.mapper.StackOverflowQuestionMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
public class JpaStackOverflowQuestionsRepositoryTest extends JpaRepositoryTest {
    StackOverflowQuestionMapper mapper = Mappers.getMapper(StackOverflowQuestionMapper.class);

    @Autowired
    JpaStackOverflowQuestionsRepository jpaStackOverflowQuestionsRepository;

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
        var stackOverflowQuestion = jpaStackOverflowQuestionsRepository.add(addParams, mapper);
        assertEquals(questionId, stackOverflowQuestion.questionId());

        entityManager.flush();
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
        var foundIds = jpaStackOverflowQuestionsRepository
                .findAll()
                .stream()
                .map(mapper::fromEntity)
                .map(StackOverflowQuestion::id)
                .toList();
        assertIterableEquals(List.of(id), foundIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveStackOverflowQuestion() {
        var id = insertStackOverflowQuestion();
        jpaStackOverflowQuestionsRepository.deleteById(id);
        entityManager.flush();

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
        jpaStackOverflowQuestionsRepository.findById(id).ifPresent(
                question -> {
                    question.setUpdatedAt(updatedAt);
                    jpaStackOverflowQuestionsRepository.saveAndFlush(question);
                }
        );
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
