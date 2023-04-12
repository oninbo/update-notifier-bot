package ru.tinkoff.edu.java.scrapper.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeAnswersResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StackOverflowClient {
    private final StackExchangeClient stackExchangeClient;

    public ListStackExchangeQuestionsResponse getStackOverflowQuestions(String version, List<Long> ids) {
        return stackExchangeClient.getStackExchangeQuestions(
                version,
                idsToString(ids),
                "stackoverflow"
        );
    }

    public ListStackExchangeAnswersResponse getStackOverflowAnswers(
            String version,
            List<Long> questionIds,
            OffsetDateTime since,
            String filter,
            Integer page
    ) {
        return stackExchangeClient.getStackExchangeAnswers(
                version,
                idsToString(questionIds),
                since.toEpochSecond(),
                filter,
                "stackoverflow",
                page
        );
    }

    private String idsToString(List<Long> ids) {
        return ids.stream().map(Object::toString).collect(Collectors.joining(";"));
    }
}
