package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.domain.jooq.tables.records.StackoverflowQuestionsRecord;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.LINKS;
import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.STACKOVERFLOW_QUESTIONS;

@Repository
@RequiredArgsConstructor
public class JooqStackOverflowQuestionsRepository implements
        BaseRepository<StackOverflowQuestion, StackOverflowQuestionAddParams> {
    private final DSLContext create;

    @Override
    public StackOverflowQuestion add(StackOverflowQuestionAddParams stackOverflowQuestionAddParams) {
        return create
                .insertInto(STACKOVERFLOW_QUESTIONS)
                .set(STACKOVERFLOW_QUESTIONS.QUESTION_ID, stackOverflowQuestionAddParams.questionId())
                .returning()
                .fetchOneInto(StackOverflowQuestion.class);
    }

    @Override
    public List<StackOverflowQuestion> findAll() {
        return create.selectFrom(STACKOVERFLOW_QUESTIONS).fetchInto(StackOverflowQuestion.class);
    }

    public Optional<StackOverflowQuestion> find(Long questionId) {
        return create
                .selectFrom(STACKOVERFLOW_QUESTIONS)
                .where(STACKOVERFLOW_QUESTIONS.QUESTION_ID.eq(questionId))
                .fetchOptionalInto(StackOverflowQuestion.class);
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(STACKOVERFLOW_QUESTIONS).where(STACKOVERFLOW_QUESTIONS.ID.eq(id)).execute();
    }

    public <T> void update(
            List<StackOverflowQuestion> repositories,
            TableField<StackoverflowQuestionsRecord, T> column,
            T value
    ) {
        var ids = repositories.stream().map(StackOverflowQuestion::id).toList();
        create
                .update(STACKOVERFLOW_QUESTIONS)
                .set(column, value)
                .where(STACKOVERFLOW_QUESTIONS.ID.in(ids))
                .execute();
    }

    public List<StackOverflowQuestion> findWithLinks(
            int first,
            TableField<StackoverflowQuestionsRecord, ?> orderColumn
    ) {
        return create
                .select(STACKOVERFLOW_QUESTIONS.asterisk())
                .from(LINKS.join(STACKOVERFLOW_QUESTIONS)
                        .on(LINKS.STACKOVERFLOW_QUESTION_ID.eq(STACKOVERFLOW_QUESTIONS.ID)))
                .orderBy(orderColumn.asc().nullsFirst())
                .limit(first)
                .fetchInto(StackOverflowQuestion.class);
    }
}
