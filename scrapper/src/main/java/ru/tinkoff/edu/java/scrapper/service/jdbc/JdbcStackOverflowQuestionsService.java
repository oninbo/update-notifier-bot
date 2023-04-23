package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.exception.StackOverflowQuestionNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowQuestionsService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JdbcStackOverflowQuestionsService
        extends StackOverflowQuestionsService
        implements FindOrDoService<StackOverflowQuestion, StackOverflowParserResult>,
        StackOverflowAnswersService {
    private final JdbcStackOverflowQuestionsRepository jdbcStackOverflowQuestionsRepository;
    private final JdbcLinksRepository jdbcLinksRepository;
    private final StackOverflowClient stackOverflowClient;
    private final ApplicationConfig applicationConfig;

    @Override
    public StackOverflowQuestion findOrCreate(StackOverflowParserResult findParams) {
        return find(findParams).orElseGet(
                () -> create(
                        new StackOverflowQuestionAddParams(
                                findParams.questionId()
                        )
                )
        );
    }

    public Optional<StackOverflowQuestion> find(StackOverflowParserResult findParams) {
        return jdbcStackOverflowQuestionsRepository.find(findParams.questionId());
    }

    public StackOverflowQuestion create(StackOverflowQuestionAddParams stackOverflowQuestion) {
        checkIfStackOverflowQuestionExists(stackOverflowQuestion, stackOverflowClient, applicationConfig);
        return jdbcStackOverflowQuestionsRepository.add(stackOverflowQuestion);
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<StackOverflowQuestion> questions) {
        return getBatchedUpdates(
                questions,
                batch -> getUpdates(
                        batch,
                        stackOverflowClient,
                        applicationConfig,
                        jdbcLinksRepository::findAllWithChatId
                ));
    }

    @Override
    public void updateUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        jdbcStackOverflowQuestionsRepository.updateUpdatedAt(questions, updatedAt);
    }

    @Override
    public List<StackOverflowQuestion> getForLinksUpdate(int first) {
        return jdbcStackOverflowQuestionsRepository.findAllWithLinks(
                first,
                JdbcStackOverflowQuestionsRepository.UpdateColumn.UPDATED_AT
        );
    }

    @Override
    public List<StackOverflowAnswerUpdate> getStackOverflowAnswerUpdates(List<StackOverflowQuestion> questions) {
        return getBatchedUpdates(
                questions,
                batch -> getAnswerUpdates(
                        batch,
                        jdbcLinksRepository::findAllWithChatId,
                        stackOverflowClient,
                        applicationConfig
                ));
    }

    @Override
    public void updateAnswersUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        jdbcStackOverflowQuestionsRepository.updateAnswersUpdatedAt(questions, updatedAt);
    }

    @Override
    public List<StackOverflowQuestion> getForAnswersUpdate(int first) {
        return jdbcStackOverflowQuestionsRepository.findAllWithLinks(
                first,
                JdbcStackOverflowQuestionsRepository.UpdateColumn.ANSWERS_UPDATED_AT
        );
    }
}
