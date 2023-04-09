package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.repository.StackOverflowQuestionsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcStackOverflowQuestionsService
        implements FindOrDoService<StackOverflowQuestion, StackOverflowParserResult> {
    private final StackOverflowQuestionsRepository stackOverflowQuestionsRepository;

    @Override
    public StackOverflowQuestion findOrThrow(StackOverflowParserResult findParams) {
        return null;
    }

    @Override
    public StackOverflowQuestion findOrCreate(StackOverflowParserResult findParams) {
        return null;
    }

    public Optional<StackOverflowQuestion> find(StackOverflowParserResult findParams) {
        return stackOverflowQuestionsRepository.find(findParams.questionId());
    }
}
