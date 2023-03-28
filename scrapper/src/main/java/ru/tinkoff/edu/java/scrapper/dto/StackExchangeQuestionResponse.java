package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackExchangeQuestionResponse(
        @JsonProperty("question_id")
        Long questionId
) {
}
