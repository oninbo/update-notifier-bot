package ru.tinkoff.edu.java.scrapper.client;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;

@RequiredArgsConstructor
public abstract class StackExchangeSiteClient {
    private final StackExchangeClient stackExchangeClient;

    protected abstract String getSite();

    public ListStackExchangeQuestionsResponse getQuestions(String version, String ids) {
        return stackExchangeClient.getStackExchangeQuestions(version, ids, getSite());
    }
}
