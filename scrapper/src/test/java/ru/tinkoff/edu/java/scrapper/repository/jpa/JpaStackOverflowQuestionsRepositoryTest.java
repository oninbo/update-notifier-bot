package ru.tinkoff.edu.java.scrapper.repository.jpa;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
public class JpaStackOverflowQuestionsRepositoryTest extends JpaRepositoryTest {
    @Autowired
    JpaStackOverflowQuestionsRepository jpaStackOverflowQuestionsRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Random
    Long questionId;

    @Random
    OffsetDateTime updatedAt;

    @Random
    Long otherQuestionId;

    @Random
    URI url;

    @Test
    @Transactional
    @Rollback
    public void shouldAddStackOverflowQuestion() {
        var addParams = new StackOverflowQuestionAddParams(questionId);
        var stackOverflowQuestion = jpaStackOverflowQuestionsRepository.add(addParams);
        assertEquals(questionId, stackOverflowQuestion.getQuestionId());

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
                .map(StackOverflowQuestionEntity::getId)
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
    @Test
    @Transactional
    @Rollback
    public void shouldFindAllGitHubRepositoriesWithLinks() {
        var tgChatId = jdbcTemplate
                .queryForObject("INSERT INTO tg_chats (chat_id) VALUES (1) RETURNING id", UUID.class);
        StackOverflowQuestionEntity otherStackOverflowQuestion = new StackOverflowQuestionEntity();
        otherStackOverflowQuestion.setQuestionId(otherQuestionId);
        entityManager.persistAndFlush(otherStackOverflowQuestion);
        LinkEntity link = new LinkEntity();
        link.setUrl(url);
        link.setTgChat(entityManager.find(TgChatEntity.class, tgChatId));
        link.setStackOverflowQuestion(otherStackOverflowQuestion);
        entityManager.persistAndFlush(link);

        var stackOverflowQuestionId = insertStackOverflowQuestion();
        var limit = 100;
        Supplier<List<UUID>> findIds = () -> jpaStackOverflowQuestionsRepository
                .findAllWithLinks(limit, JpaStackOverflowQuestionsRepository.OrderColumn.updatedAt)
                .stream()
                .map(StackOverflowQuestionEntity::getId)
                .toList();
        var foundIds = findIds.get();
        assertIterableEquals(List.of(otherStackOverflowQuestion.getId()), foundIds);

        jdbcTemplate.update(
                "INSERT INTO links (url, tg_chat_id, stackoverflow_question_id) VALUES (?, ?, ?)",
                "test",
                tgChatId,
                stackOverflowQuestionId
        );
        foundIds = findIds.get();
        assertIterableEquals(List.of(stackOverflowQuestionId, otherStackOverflowQuestion.getId()), foundIds);
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
