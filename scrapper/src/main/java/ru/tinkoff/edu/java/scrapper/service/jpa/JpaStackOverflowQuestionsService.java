package ru.tinkoff.edu.java.scrapper.service.jpa;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;
import ru.tinkoff.edu.java.scrapper.mapper.StackOverflowQuestionMapper;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaStackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowQuestionsService;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class JpaStackOverflowQuestionsService
        extends StackOverflowQuestionsService
        implements FindOrDoService<StackOverflowQuestionEntity, StackOverflowParserResult>,
        StackOverflowAnswersService {
    private final JpaStackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final JpaLinksRepository linksRepository;
    private final ApplicationConfig applicationConfig;
    private final StackOverflowClient stackOverflowClient;
    private final StackOverflowQuestionMapper mapper;

    @Override
    public StackOverflowQuestionEntity findOrCreate(StackOverflowParserResult findParams) {
        return stackOverflowQuestionsRepository.findByQuestionId(findParams.questionId())
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
        var ids = questions.stream().map(StackOverflowQuestion::id).toList();
        var entities = stackOverflowQuestionsRepository.findAllById(ids);
        entities.forEach(e -> e.setUpdatedAt(updatedAt));
        stackOverflowQuestionsRepository.saveAllAndFlush(entities);
    }

    @Override
    public List<StackOverflowQuestion> getForLinksUpdate(int first) {
        return stackOverflowQuestionsRepository.findAllWithLinks(
                        first,
                        JpaStackOverflowQuestionsRepository.OrderColumn.updatedAt
                )
                .stream().map(mapper::fromEntity)
                .toList();
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
        var ids = questions.stream().map(StackOverflowQuestion::id).toList();
        var entities = stackOverflowQuestionsRepository.findAllById(ids);
        entities.forEach(e -> e.setAnswersUpdatedAt(updatedAt));
        stackOverflowQuestionsRepository.saveAllAndFlush(entities);
    }

    @Override
    public List<StackOverflowQuestion> getForAnswersUpdate(int first) {
        return stackOverflowQuestionsRepository.findAllWithLinks(
                        first,
                        JpaStackOverflowQuestionsRepository.OrderColumn.answersUpdatedAt
                )
                .stream().map(mapper::fromEntity)
                .toList();
    }
}
