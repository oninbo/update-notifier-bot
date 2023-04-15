package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.domain.jooq.tables.pojos.StackoverflowQuestions;
import ru.tinkoff.edu.java.scrapper.domain.jooq.tables.records.StackoverflowQuestionsRecord;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.*;

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
        var question = create
                .selectFrom(STACKOVERFLOW_QUESTIONS)
                .where(STACKOVERFLOW_QUESTIONS.QUESTION_ID.eq(questionId))
                .fetchOneInto(StackOverflowQuestion.class);
        return Optional.ofNullable(question);
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(STACKOVERFLOW_QUESTIONS).where(STACKOVERFLOW_QUESTIONS.ID.eq(id)).execute();
    }

    public void update(StackOverflowQuestion question, Consumer<StackoverflowQuestions> setter) {
        var record = create
                .selectFrom(STACKOVERFLOW_QUESTIONS)
                .where(STACKOVERFLOW_QUESTIONS.ID.eq(question.id()))
                .fetchOneInto(StackoverflowQuestions.class);
        Optional.ofNullable(record)
                .ifPresent(s -> {
                    setter.accept(s);
                    create.newRecord(STACKOVERFLOW_QUESTIONS, s).store();
                });
    }

    public void update(List<StackOverflowQuestion> questions, Consumer<StackoverflowQuestions> setter) {
        var ids = questions.stream().map(StackOverflowQuestion::id).toList();
        var records = create
                .selectFrom(STACKOVERFLOW_QUESTIONS)
                .where(STACKOVERFLOW_QUESTIONS.ID.in(ids))
                .fetchInto(StackoverflowQuestions.class)
                .stream()
                .map(q -> {
                    setter.accept(q);
                    return create.newRecord(STACKOVERFLOW_QUESTIONS, q);
                })
                .toList();
        create.batchUpdate(records).execute();
    }

    public List<StackOverflowQuestion> findWithLinks(
            int first,
            TableField<StackoverflowQuestionsRecord, ?> orderColumn
    ) {
        return create
                .select(GITHUB_REPOSITORIES.asterisk())
                .from(LINKS.join(STACKOVERFLOW_QUESTIONS)
                        .on(LINKS.STACKOVERFLOW_QUESTION_ID.eq(STACKOVERFLOW_QUESTIONS.ID)))
                .orderBy(orderColumn.asc().nullsFirst())
                .limit(first)
                .fetchInto(StackOverflowQuestion.class);
    }
}
