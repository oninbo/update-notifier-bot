package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;

import java.time.OffsetDateTime;
import java.util.List;

public interface StackOverflowAnswersService extends LinksUpdatesService<StackOverflowQuestion> {
    List<StackOverflowAnswerUpdate> getStackOverflowAnswerUpdates(
            List<StackOverflowQuestion> questions
    );

    void updateAnswersUpdatedAt(List<StackOverflowQuestion> questions, OffsetDateTime updatedAt);
    List<StackOverflowQuestion> getForAnswersUpdate(int first);
}
