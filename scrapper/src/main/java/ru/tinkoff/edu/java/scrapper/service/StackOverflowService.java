package ru.tinkoff.edu.java.scrapper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.StackExchangeQuestionResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StackOverflowService {
    private final StackOverflowClient stackOverflowClient;
    private final ApplicationConfig applicationConfig;

    public StackExchangeQuestionResponse getQuestionFromApi(Long id) {
        return stackOverflowClient
                .getStackOverflowQuestions(stackExchangeApiVersion(), List.of(id))
                .items()
                .get(0);
    }

    public List<StackExchangeQuestionResponse> getQuestionsFromApi(List<Long> ids) {
        return stackOverflowClient
                .getStackOverflowQuestions(stackExchangeApiVersion(), ids)
                .items();
    }

    private String stackExchangeApiVersion() {
        return applicationConfig.webClient().stackExchange().apiVersion();
    }
}
