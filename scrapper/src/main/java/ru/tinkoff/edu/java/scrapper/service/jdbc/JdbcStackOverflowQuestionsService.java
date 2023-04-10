package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.exception.StackOverflowQuestionNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.StackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcStackOverflowQuestionsService
        implements FindOrDoService<StackOverflowQuestion, StackOverflowParserResult> {
    private final StackOverflowQuestionsRepository stackOverflowQuestionsRepository;
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
}
