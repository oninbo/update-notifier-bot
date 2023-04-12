package ru.tinkoff.edu.java.scrapper.service;

import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;

import java.util.List;

public interface StackOverflowAnswersService extends UpdatesService<StackOverflowQuestion> {
    List<StackOverflowAnswerUpdate> getStackOverflowAnswerUpdates(
            List<StackOverflowQuestion> questions
    );
}
