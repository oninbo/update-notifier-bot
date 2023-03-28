package ru.tinkoff.edu.java.scrapper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.ClientConfiguration;
import ru.tinkoff.edu.java.scrapper.configuration.WebClient;
import ru.tinkoff.edu.java.scrapper.configuration.WebClientConfig;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;
import ru.tinkoff.edu.java.scrapper.dto.StackExchangeQuestionResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackOverflowClientTest {

    public static MockWebServer mockBackEnd;
    public StackOverflowClient stackOverflowClient;

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
        WebClient webClient = new WebClient(null, new WebClientConfig(baseUrl, "1"));
        ApplicationConfig applicationConfig = new ApplicationConfig(null, webClient, null);
        StackExchangeClient stackExchangeClient = new ClientConfiguration().getStackExchangeClient(applicationConfig);
        stackOverflowClient = new StackOverflowClient(stackExchangeClient);
    }

    @Test
    public void shouldGetListOfQuestions() throws InterruptedException, JsonProcessingException {
        StackExchangeQuestionResponse mockQuestion1 = new StackExchangeQuestionResponse(10L);
        StackExchangeQuestionResponse mockQuestion2 = new StackExchangeQuestionResponse(20L);
        ListStackExchangeQuestionsResponse mockQuestionList =
                new ListStackExchangeQuestionsResponse(List.of(mockQuestion1, mockQuestion2));
        ObjectMapper mapper = new ObjectMapper();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockQuestionList))
                .addHeader("Content-Type", "application/json"));
        var result = stackOverflowClient.getStackOverflowQuestions("1", List.of(10L, 20L));
        assertEquals(result, mockQuestionList);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        String expectedPath = String.format("/1/questions/%s?site=stackoverflow",
                URLEncoder.encode("10;20", StandardCharsets.UTF_8));
        assertEquals(expectedPath, recordedRequest.getPath());
    }
}
