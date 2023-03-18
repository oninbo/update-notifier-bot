package ru.tinkoff.edu.java.scrapper.client;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class StackExchangeSiteClient {
    private final StackExchangeClient stackExchangeClient;

    protected abstract String getSite();

    public ListStackExchangeQuestionsResponse getQuestions(String version, List<Long> ids) {
        String idsString = ids.stream().map(Object::toString).collect(Collectors.joining(";"));
        return stackExchangeClient.getStackExchangeQuestions(version, idsString, getSite());
    }
}
