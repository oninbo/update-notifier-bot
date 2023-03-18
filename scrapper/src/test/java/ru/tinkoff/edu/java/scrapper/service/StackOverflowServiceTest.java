package ru.tinkoff.edu.java.scrapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.edu.java.scrapper.client.StackExchangeClient;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.ClientConfiguration;
import ru.tinkoff.edu.java.scrapper.configuration.WebClient;
import ru.tinkoff.edu.java.scrapper.configuration.WebClientData;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;
import ru.tinkoff.edu.java.scrapper.dto.StackExchangeQuestionResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackOverflowServiceTest {
    public static MockWebServer mockBackEnd;
    public StackOverflowService stackOverflowService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        WebClient webClient = new WebClient(null, new WebClientData(baseUrl, "1"));
        ApplicationConfig applicationConfig = new ApplicationConfig(null, webClient);
        StackExchangeClient stackExchangeClient = new ClientConfiguration().getStackExchangeClient(applicationConfig);
        StackOverflowClient stackOverflowClient = new StackOverflowClient(stackExchangeClient);
        stackOverflowService = new StackOverflowService(stackOverflowClient, applicationConfig);
    }

    @Test
    public void shouldGetQuestion() throws InterruptedException, JsonProcessingException {
        StackExchangeQuestionResponse mockQuestion = new StackExchangeQuestionResponse(List.of("spring", "java"));
        ListStackExchangeQuestionsResponse mockQuestionList =
                new ListStackExchangeQuestionsResponse(List.of(mockQuestion));
        ObjectMapper mapper = new ObjectMapper();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockQuestionList))
                .addHeader("Content-Type", "application/json"));
        var result = stackOverflowService.getQuestionFromApi(10L);
        assertEquals(result, mockQuestion);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/1/questions/10?site=stackoverflow", recordedRequest.getPath());
    }
    @Test
    public void shouldGetListOfQuestions() throws InterruptedException, JsonProcessingException {
        StackExchangeQuestionResponse mockQuestion1 = new StackExchangeQuestionResponse(List.of("spring", "java"));
        StackExchangeQuestionResponse mockQuestion2 = new StackExchangeQuestionResponse(List.of("android", "okhttp"));
        ListStackExchangeQuestionsResponse mockQuestionList =
                new ListStackExchangeQuestionsResponse(List.of(mockQuestion1, mockQuestion2));
        ObjectMapper mapper = new ObjectMapper();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockQuestionList))
                .addHeader("Content-Type", "application/json"));
        var result = stackOverflowService.getQuestionsFromApi(List.of(10L, 20L));
        assertEquals(result, mockQuestionList.items());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        String expectedPath = String.format("/1/questions/%s?site=stackoverflow",
                URLEncoder.encode("10;20", StandardCharsets.UTF_8));
        assertEquals(expectedPath, recordedRequest.getPath());
    }
}
