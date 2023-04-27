package ru.tinkoff.edu.java.scrapper.mapper;

import org.mapstruct.Mapper;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;

@Mapper
public interface StackOverflowQuestionMapper {
    StackOverflowQuestion fromEntity(StackOverflowQuestionEntity entity);
    StackOverflowQuestionEntity toEntity(StackOverflowQuestion gitHubRepository);
}
