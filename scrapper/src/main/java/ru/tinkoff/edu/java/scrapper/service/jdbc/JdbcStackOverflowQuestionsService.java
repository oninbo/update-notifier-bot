package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.exception.StackOverflowQuestionNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.LinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.StackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.LinkUpdateUtils;
import ru.tinkoff.edu.java.scrapper.service.UpdatesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcStackOverflowQuestionsService implements
        FindOrDoService<StackOverflowQuestion, StackOverflowParserResult>,
        UpdatesService {
    private final StackOverflowQuestionsRepository stackOverflowQuestionsRepository;
    private final LinksRepository linksRepository;
    private final StackOverflowClient stackOverflowClient;
    private final ApplicationConfig applicationConfig;

    @Override
    public StackOverflowQuestion findOrThrow(StackOverflowParserResult findParams) {
        return find(findParams).orElseThrow(() -> new StackOverflowQuestionNotFoundException(applicationConfig));
    }

    @Override
    public StackOverflowQuestion findOrCreate(StackOverflowParserResult findParams) {
        return find(findParams).orElse(
                create(
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
    public List<LinkUpdate> getUpdates() {
        int batchSize = 100;
        List<StackOverflowQuestion> questions = stackOverflowQuestionsRepository.findAllWithLinks();
        List<LinkUpdate> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i += batchSize) {
            var batch = questions.subList(i, Math.min(i + batchSize, questions.size()));
            var fetchedBatch = stackOverflowClient.getStackOverflowQuestions(
                    applicationConfig.webClient().stackExchange().apiVersion(),
                    batch.stream().map(StackOverflowQuestion::questionId).toList()
            );
            List<LinkUpdate> batchResult = LinkUpdateUtils.getUpdates(
                    batch,
                    q -> fetchedBatch.items().stream()
                            .filter(item -> item.questionId().equals(q.questionId()))
                            .findFirst()
                            .map(s -> ObjectUtils.max(s.lastActivityDate(), s.lastEditDate()))
                            .orElse(null),
                    linksRepository::findAllWithChatId,
                    StackOverflowQuestion::updatedAt
            );
            result.addAll(batchResult);
        }
        return result;
    }
}
