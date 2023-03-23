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
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryResponse;
import ru.tinkoff.edu.java.scrapper.dto.GitHubUserResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitHubClientTest {

    public static MockWebServer mockBackEnd;
    public GitHubClient gitHubClient;

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
        WebClient webClient = new WebClient(new WebClientConfig(baseUrl, "1"), null);
        ApplicationConfig applicationConfig = new ApplicationConfig(null, webClient, null);
        gitHubClient = new ClientConfiguration().getGitHubClient(applicationConfig);
    }

    @Test
    public void shouldGetGitHubRepository() throws InterruptedException, JsonProcessingException {
        GitHubUserResponse mockUser = new GitHubUserResponse("mock_user", 1L);
        GitHubRepositoryResponse mockRepository = new GitHubRepositoryResponse("mock_project", mockUser);
        ObjectMapper mapper = new ObjectMapper();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockRepository))
                .addHeader("Content-Type", "application/json"));
        var result = gitHubClient.getRepository(mockUser.login(), mockRepository.name(), "1");
        assertEquals(result, mockRepository);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/repos/mock_user/mock_project?X-GitHub-Api-Version=1", recordedRequest.getPath());
    }
}
