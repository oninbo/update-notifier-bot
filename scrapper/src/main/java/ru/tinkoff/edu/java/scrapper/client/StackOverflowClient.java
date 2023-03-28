package ru.tinkoff.edu.java.scrapper.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StackOverflowClient {
    private final StackExchangeClient stackExchangeClient;

    public ListStackExchangeQuestionsResponse getStackOverflowQuestions(String version, List<Long> ids) {
        String idsString = ids.stream().map(Object::toString).collect(Collectors.joining(";"));
        return stackExchangeClient.getStackExchangeQuestions(version, idsString, "stackoverflow");
    }
}
