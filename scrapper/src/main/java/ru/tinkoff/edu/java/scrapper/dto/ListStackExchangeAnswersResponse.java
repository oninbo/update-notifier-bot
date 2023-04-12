package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListStackExchangeAnswersResponse(
        List<StackExchangeAnswerResponse> items,
        @JsonProperty("has_more")
        Boolean hasMore
        ) {
}
