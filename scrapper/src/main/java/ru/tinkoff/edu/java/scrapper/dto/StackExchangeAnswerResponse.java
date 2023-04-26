package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.OffsetDateTime;

public record StackExchangeAnswerResponse(
        @JsonProperty("question_id")
        Long questionId,
        @JsonProperty("answer_id")
        Long answerId,
        URI link,
        @JsonProperty("creation_date")
        OffsetDateTime creationDate
) {
}
