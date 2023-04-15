package ru.tinkoff.edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record StackExchangeQuestionResponse(
        @JsonProperty("question_id")
        Long questionId,
        @JsonProperty("last_activity_date")
        OffsetDateTime lastActivityDate,
        @JsonProperty("last_edit_date")
        OffsetDateTime lastEditDate
) {
}
