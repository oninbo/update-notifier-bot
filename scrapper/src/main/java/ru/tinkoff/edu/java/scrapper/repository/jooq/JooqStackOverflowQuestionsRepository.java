package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.STACKOVERFLOW_QUESTIONS;

@Repository
@RequiredArgsConstructor
public class JooqStackOverflowQuestionsRepository implements
        BaseRepository<StackOverflowQuestion, StackOverflowQuestionAddParams> {
    private final DSLContext create;

    @Override
    public StackOverflowQuestion add(StackOverflowQuestionAddParams stackOverflowQuestionAddParams) {
        var result = create
                .insertInto(STACKOVERFLOW_QUESTIONS)
                .set(STACKOVERFLOW_QUESTIONS.QUESTION_ID, stackOverflowQuestionAddParams.questionId())
                .returning()
                .fetchOne();
        //noinspection DataFlowIssue
        return result.into(StackOverflowQuestion.class);
    }

    @Override
    public List<StackOverflowQuestion> findAll() {
        return create.selectFrom(STACKOVERFLOW_QUESTIONS).fetchInto(StackOverflowQuestion.class);
    }

    public Optional<StackOverflowQuestion> find(Long questionId) {
        var record = create
                .selectFrom(STACKOVERFLOW_QUESTIONS)
                .where(STACKOVERFLOW_QUESTIONS.QUESTION_ID.eq(questionId))
                .fetchOne();
        return Optional.ofNullable(record).map(r -> r.into(StackOverflowQuestion.class));
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(STACKOVERFLOW_QUESTIONS).where(STACKOVERFLOW_QUESTIONS.ID.eq(id)).execute();
    }
}
