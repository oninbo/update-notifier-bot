package ru.tinkoff.edu.java.scrapper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        WebClient webClient = mock(WebClient.class);
        when(webClient.github()).thenReturn(new WebClientConfig(baseUrl, "1"));
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.webClient()).thenReturn(webClient);
        gitHubClient = new ClientConfiguration().getGitHubClient(applicationConfig);
    }

    @Test
    public void shouldGetGitHubRepository() throws InterruptedException, JsonProcessingException {
        GitHubUserResponse mockUser = new GitHubUserResponse("mock_user", 1L);
        GitHubRepositoryResponse mockRepository = mock(GitHubRepositoryResponse.class);
        when(mockRepository.updatedAt()).thenReturn(OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC));
        when(mockRepository.pushedAt()).thenReturn(OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC));
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockRepository))
                .addHeader("Content-Type", "application/json"));
        var result = gitHubClient.getRepository(mockUser.login(), "mock_project", "1");
        assertEquals(result.updatedAt(), mockRepository.updatedAt());
        assertEquals(result.pushedAt(), mockRepository.pushedAt());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/repos/mock_user/mock_project?X-GitHub-Api-Version=1", recordedRequest.getPath());
    }
}
