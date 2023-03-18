package ru.tinkoff.edu.java.scrapper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.StackExchangeQuestionResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StackOverflowService {
    private final StackOverflowClient stackOverflowClient;
    private final ApplicationConfig applicationConfig;

    public StackExchangeQuestionResponse getQuestionFromApi(Long id) {
        return stackOverflowClient
                .getQuestions(stackExchangeApiVersion(), id.toString())
                .items()
                .get(0);
    }

    public List<StackExchangeQuestionResponse> getQuestionsFromApi(List<Long> ids) {
        String idsString = ids.stream().map(Object::toString).collect(Collectors.joining(";"));
        return stackOverflowClient
                .getQuestions(stackExchangeApiVersion(), idsString)
                .items();
    }

    private String stackExchangeApiVersion() {
        return applicationConfig.webClient().stackExchange().apiVersion();
    }
}
