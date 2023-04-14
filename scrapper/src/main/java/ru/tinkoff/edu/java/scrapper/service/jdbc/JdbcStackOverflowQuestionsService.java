package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.client.StackExchangeClient;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.StackOverflowQuestionNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.StackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.StackOverflowAnswersService;
import ru.tinkoff.edu.java.scrapper.service.UpdatesService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkUpdateUtils;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JdbcStackOverflowQuestionsService implements
        FindOrDoService<StackOverflowQuestion, StackOverflowParserResult>,
        UpdatesService<StackOverflowQuestion>,
        StackOverflowAnswersService {
    private final StackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final JdbcLinksRepository jdbcLinksRepository;
    private final StackOverflowClient stackOverflowClient;
    private final ApplicationConfig applicationConfig;

    @Override
    public StackOverflowQuestion findOrThrow(StackOverflowParserResult findParams) {
        return find(findParams).orElseThrow(() -> new StackOverflowQuestionNotFoundException(applicationConfig));
    }

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
        return stackOverflowQuestionsRepository.find(findParams.questionId());
    }

    public StackOverflowQuestion create(StackOverflowQuestionAddParams stackOverflowQuestion) {
        checkIfStackOverflowQuestionExists(stackOverflowQuestion);
        return stackOverflowQuestionsRepository.add(stackOverflowQuestion);
    }

    private void checkIfStackOverflowQuestionExists(StackOverflowQuestionAddParams stackOverflowQuestion) {
        var response = stackOverflowClient.getStackOverflowQuestions(
                applicationConfig.webClient().stackExchange().apiVersion(),
                List.of(stackOverflowQuestion.questionId())
        );
        if (response.items().isEmpty()) {
            throw new StackOverflowQuestionNotFoundException(applicationConfig);
        }
    }

    @Override
    public List<LinkUpdate> getLinkUpdates(List<StackOverflowQuestion> questions) {
        int batchSize = 100;
        List<LinkUpdate> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i += batchSize) {
            var batch = questions.subList(i, Math.min(i + batchSize, questions.size()));
            var fetchedBatch = stackOverflowClient.getStackOverflowQuestions(
                    applicationConfig.webClient().stackExchange().apiVersion(),
                    batch.stream().map(StackOverflowQuestion::questionId).toList()
            );
            List<LinkUpdate> batchResult = LinkUpdateUtils.getUpdates(
                    batch,
                    question -> fetchedBatch.items().stream()
                            .filter(item -> item.questionId().equals(question.questionId()))
                            .findFirst()
                            .map(sq -> ObjectUtils.max(sq.lastActivityDate(), sq.lastEditDate()))
                            .orElse(null),
                    jdbcLinksRepository::findAllWithChatId,
                    StackOverflowQuestion::updatedAt,
                    StackOverflowQuestion::createdAt
            );
            result.addAll(batchResult);
        }
        return result;
    }

    @Override
    public void updateUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        stackOverflowQuestionsRepository.updateUpdatedAt(questions, updatedAt);
    }

    @Override
    public List<StackOverflowQuestion> getObjectsForUpdate(int first) {
        return stackOverflowQuestionsRepository.findAllWithLinks(
                first,
                StackOverflowQuestionsRepository.UpdateColumn.UPDATED_AT
        );
    }

    @Override
    public List<StackOverflowAnswerUpdate> getStackOverflowAnswerUpdates(
            List<StackOverflowQuestion> questions
    ) {
        var since = questions.stream()
                .map(StackOverflowQuestion::answersUpdatedAt)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        var ids = questions.stream().map(StackOverflowQuestion::questionId).toList();

        List<StackExchangeAnswerResponse> answers = new ArrayList<>();
        ListStackExchangeAnswersResponse response;
        int page = 1;
        do {
            response = stackOverflowClient.getStackOverflowAnswers(
                    applicationConfig.webClient().stackExchange().apiVersion(),
                    ids,
                    since,
                    StackExchangeClient.ANSWER_LINK_FILTER,
                    page
            );
            answers.addAll(response.items());
            page++;
        } while (response.hasMore());

        List<StackOverflowAnswerUpdate> result = new ArrayList<>();
        answers.forEach(
                answer -> {
                    var questionResult = questions
                            .stream()
                            .filter(q -> q.questionId().equals(answer.questionId()))
                            .findFirst();
                    if (questionResult.isEmpty()) {
                        return;
                    }
                    var question = questionResult.get();
                    if (!question.answersUpdatedAt().isBefore(answer.creationDate())) {
                        return;
                    }
                    var links = jdbcLinksRepository.findAllWithChatId(question);
                    if (links.isEmpty()) {
                        return;
                    }
                    var link = links.get(0);
                    result.add(
                            new StackOverflowAnswerUpdate(
                                    link.url(),
                                    answer.link(),
                                    links.stream().map(LinkWithChatId::chatId).toList()
                            )
                    );
                }
        );
        return result;
    }

    @Override
    public void updateAnswersUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt) {
        stackOverflowQuestionsRepository.updateAnswersUpdatedAt(questions, updatedAt);
    }

    @Override
    public List<StackOverflowQuestion> getQuestionsForUpdate(int first) {
        return stackOverflowQuestionsRepository.findAllWithLinks(
                first,
                StackOverflowQuestionsRepository.UpdateColumn.ANSWERS_UPDATED_AT
        );
    }
}
