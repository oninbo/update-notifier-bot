package ru.tinkoff.edu.java.scrapper.service.jooq;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowQuestionsService;

import java.time.OffsetDateTime;
import java.util.List;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.STACKOVERFLOW_QUESTIONS;

@RequiredArgsConstructor
public class JooqStackOverflowQuestionsService
        extends StackOverflowQuestionsService
        implements FindOrDoService<StackOverflowQuestion, StackOverflowParserResult>,
        StackOverflowAnswersService {
    private final JooqStackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final JooqLinksRepository linksRepository;
    private final ApplicationConfig applicationConfig;
    private final StackOverflowClient stackOverflowClient;

    @Override
    public StackOverflowQuestion findOrCreate(StackOverflowParserResult findParams) {
        return stackOverflowQuestionsRepository.find(findParams.questionId())
                .orElseGet(() -> {
                    var addParams = new StackOverflowQuestionAddParams(findParams.questionId());
                    checkIfStackOverflowQuestionExists(addParams, stackOverflowClient, applicationConfig);
                    return stackOverflowQuestionsRepository.add(addParams);
                });
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<StackOverflowQuestion> questions) {
        return getBatchedUpdates(
                questions,
                batch -> getUpdates(
                        batch,
                        stackOverflowClient,
                        applicationConfig,
                        linksRepository::findAllWithChatId
                ));
    }

    @Override
    public void updateUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        stackOverflowQuestionsRepository.update(questions, STACKOVERFLOW_QUESTIONS.UPDATED_AT, updatedAt);
    }

    @Override
    public List<StackOverflowQuestion> getForLinksUpdate(int first) {
        return stackOverflowQuestionsRepository.findWithLinks(first, STACKOVERFLOW_QUESTIONS.UPDATED_AT);
    }

    @Override
    public List<StackOverflowAnswerUpdate> getStackOverflowAnswerUpdates(List<StackOverflowQuestion> questions) {
        return getBatchedUpdates(
                questions,
                batch -> getAnswerUpdates(
                        batch,
                        linksRepository::findAllWithChatId,
                        stackOverflowClient,
                        applicationConfig
                ));
    }

    @Override
    public void updateAnswersUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        stackOverflowQuestionsRepository.update(questions, STACKOVERFLOW_QUESTIONS.ANSWERS_UPDATED_AT, updatedAt);
    }

    @Override
    public List<StackOverflowQuestion> getForAnswersUpdate(int first) {
        return stackOverflowQuestionsRepository.findWithLinks(first, STACKOVERFLOW_QUESTIONS.ANSWERS_UPDATED_AT);
    }
}
