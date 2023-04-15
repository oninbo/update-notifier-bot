package ru.tinkoff.edu.java.scrapper.service.utils;

import ru.tinkoff.edu.java.scrapper.client.StackExchangeClient;
import ru.tinkoff.edu.java.scrapper.client.StackOverflowClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class StackOverflowAnswersUtils {
    public static <T> List<T> getBatchedUpdates(
            List<StackOverflowQuestion> questions,
            Function<List<StackOverflowQuestion>, List<T>> getUpdates
    ) {
        int batchSize = 100;
        List<T> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i += batchSize) {
            var batch = questions.subList(i, Math.min(i + batchSize, questions.size()));
            result.addAll(getUpdates.apply(batch));
        }
        return result;
    }

    public static List<StackOverflowAnswerUpdate> getAnswerUpdates(
            List<StackOverflowQuestion> questions,
            Function<StackOverflowQuestion, List<LinkWithChatId>> getLinks,
            StackOverflowClient stackOverflowClient,
            ApplicationConfig applicationConfig
    ) {
        var answers = StackOverflowAnswersUtils.getAnswers(questions, stackOverflowClient, applicationConfig);
        return answers
                .stream()
                .map(answer ->
                        StackOverflowAnswersUtils.getAnswerUpdate(
                                answer,
                                questions,
                                getLinks))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private static List<StackExchangeAnswerResponse> getAnswers(
            List<StackOverflowQuestion> questions,
            StackOverflowClient stackOverflowClient,
            ApplicationConfig applicationConfig
    ) {
        var since = questions.stream()
                .map(StackOverflowQuestion::answersUpdatedAt)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        var ids = questions.stream().map(StackOverflowQuestion::questionId).toList();
        List<StackExchangeAnswerResponse> answers = new ArrayList<>();
        ListStackExchangeAnswersResponse response;
        int page = 1;
        do {
            response = stackOverflowClient.getStackOverflowAnswers(
                    applicationConfig.webClient().stackExchange().apiVersion(),
                    ids,
                    since,
                    StackExchangeClient.ANSWER_LINK_FILTER,
                    page
            );
            answers.addAll(response.items());
            page++;
        } while (response.hasMore());
        return answers;
    }

    private static Optional<StackOverflowAnswerUpdate> getAnswerUpdate(
            StackExchangeAnswerResponse answer,
            List<StackOverflowQuestion> questions,
            Function<StackOverflowQuestion, List<LinkWithChatId>> getLinks
    ) {
        var questionResult = questions
                .stream()
                .filter(q -> q.questionId().equals(answer.questionId()))
                .findFirst();
        if (questionResult.isEmpty()) {
            return Optional.empty();
        }
        var question = questionResult.get();
        if (!question.answersUpdatedAt().isBefore(answer.creationDate())) {
            return Optional.empty();
        }
        var links = getLinks.apply(question);
        if (links.isEmpty()) {
            return Optional.empty();
        }
        var link = links.get(0);
        return Optional.of(
                new StackOverflowAnswerUpdate(
                        link.url(),
                        answer.link(),
                        links.stream().map(LinkWithChatId::chatId).toList()
                )
        );
    }
}
