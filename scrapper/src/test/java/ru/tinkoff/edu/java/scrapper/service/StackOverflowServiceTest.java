package ru.tinkoff.edu.java.scrapper.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.edu.java.scrapper.client.StackExchangeClient;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.WebClient;
import ru.tinkoff.edu.java.scrapper.configuration.WebClientSettings;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;
import ru.tinkoff.edu.java.scrapper.dto.StackExchangeQuestionResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackOverflowServiceTest {
    public StackOverflowService stackOverflowService;
    public ListStackExchangeQuestionsResponse mockQuestionList;

    @BeforeEach
    void initialize() {
        WebClient webClient = new WebClient(null, new WebClientSettings(null, "1"));
        ApplicationConfig applicationConfig = new ApplicationConfig(null, webClient);

        StackExchangeQuestionResponse mockQuestion1 = new StackExchangeQuestionResponse(List.of("spring", "java"));
        StackExchangeQuestionResponse mockQuestion2 = new StackExchangeQuestionResponse(List.of("android", "okhttp"));
        mockQuestionList = new ListStackExchangeQuestionsResponse(List.of(mockQuestion1, mockQuestion2));

        StackExchangeClient stackExchangeClient = (version, ids, site) -> mockQuestionList;
        StackOverflowClient stackOverflowClient = new StackOverflowClient(stackExchangeClient);
        stackOverflowService = new StackOverflowService(stackOverflowClient, applicationConfig);
    }

    @Test
    public void shouldGetQuestion() {
        var result = stackOverflowService.getQuestionFromApi(10L);
        assertEquals(result, mockQuestionList.items().get(0));
    }

    @Test
    public void shouldGetListOfQuestions() {
        var result = stackOverflowService.getQuestionsFromApi(List.of(10L, 20L));
        assertEquals(result, mockQuestionList.items());
    }
}
