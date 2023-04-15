package ru.tinkoff.edu.java.scrapper.service.utils;

import org.apache.commons.lang3.ObjectUtils;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.LinkWithChatId;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;

import java.util.List;
import java.util.function.Function;

public class StackOverflowLinksUtils {
    public static List<LinkUpdate> getUpdates(
            List<StackOverflowQuestion> questions,
            StackOverflowClient stackOverflowClient,
            ApplicationConfig applicationConfig,
            Function<StackOverflowQuestion, List<LinkWithChatId>> getLinks
    ) {
        var fetchedBatch = stackOverflowClient.getStackOverflowQuestions(
                applicationConfig.webClient().stackExchange().apiVersion(),
                questions.stream().map(StackOverflowQuestion::questionId).toList()
        );
        return LinkUpdateUtils.getUpdates(
                questions,
                question -> fetchedBatch.items().stream()
                        .filter(item -> item.questionId().equals(question.questionId()))
                        .findFirst()
                        .map(sq -> ObjectUtils.max(sq.lastActivityDate(), sq.lastEditDate()))
                        .orElse(null),
                getLinks,
                StackOverflowQuestion::updatedAt,
                StackOverflowQuestion::createdAt
        );
    }
}
