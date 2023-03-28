package ru.tinkoff.edu.java.scrapper.dto;

import java.util.List;

public record ListStackExchangeQuestionsResponse(List<StackExchangeQuestionResponse> items) {
}
