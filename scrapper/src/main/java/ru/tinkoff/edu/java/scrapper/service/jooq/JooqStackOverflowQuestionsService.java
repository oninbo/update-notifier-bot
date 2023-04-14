package ru.tinkoff.edu.java.scrapper.service.jooq;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.exception.StackOverflowQuestionNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;

import java.time.OffsetDateTime;
import java.util.List;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.STACKOVERFLOW_QUESTIONS;

@Service
@RequiredArgsConstructor
class JooqStackOverflowQuestionsService  implements
        FindOrDoService<StackOverflowQuestion, StackOverflowParserResult>,
        StackOverflowAnswersService {
    private final JooqStackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final ApplicationConfig applicationConfig;

    @Override
    public StackOverflowQuestion findOrThrow(StackOverflowParserResult findParams) {
        return stackOverflowQuestionsRepository.find(findParams.questionId())
                .orElseThrow(() -> new StackOverflowQuestionNotFoundException(applicationConfig));
    }

    @Override
    public StackOverflowQuestion findOrCreate(StackOverflowParserResult findParams) {
        return stackOverflowQuestionsRepository.find(findParams.questionId())
                .orElseGet(() -> stackOverflowQuestionsRepository
                        .add(new StackOverflowQuestionAddParams(findParams.questionId())));
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<StackOverflowQuestion> objects) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void updateUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        stackOverflowQuestionsRepository.update(questions,
                stackoverflowQuestions -> stackoverflowQuestions.setUpdatedAt(updatedAt));
    }

    @Override
    public List<StackOverflowQuestion> getForLinksUpdate(int first) {
        return stackOverflowQuestionsRepository.findWithLinks(first, STACKOVERFLOW_QUESTIONS.UPDATED_AT);
    }

    @Override
    public List<StackOverflowAnswerUpdate> getStackOverflowAnswerUpdates(List<StackOverflowQuestion> questions) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void updateAnswersUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        stackOverflowQuestionsRepository.update(questions,
                stackoverflowQuestions -> stackoverflowQuestions.setAnswersUpdatedAt(updatedAt));
    }

    @Override
    public List<StackOverflowQuestion> getForAnswersUpdate(int first) {
        return stackOverflowQuestionsRepository.findWithLinks(first, STACKOVERFLOW_QUESTIONS.ANSWERS_UPDATED_AT);
    }

    public void updateAllTimestamps(StackOverflowQuestion stackOverflowQuestion, OffsetDateTime now) {
        stackOverflowQuestionsRepository.update(stackOverflowQuestion,
                stackoverflowQuestions -> {
                    stackoverflowQuestions.setUpdatedAt(now);
                    stackoverflowQuestions.setAnswersUpdatedAt(now);
                });
    }
}
