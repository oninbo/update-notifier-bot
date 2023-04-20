package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;

import java.util.UUID;

public interface JpaStackOverflowQuestionsRepository extends JpaRepository<StackOverflowQuestionEntity, UUID> {
    default StackOverflowQuestion add(StackOverflowQuestionAddParams addParams) {
        var entity = new StackOverflowQuestionEntity();
        entity.setQuestionId(addParams.questionId());
        save(entity);
        return mapEntity(entity);
    }


    default StackOverflowQuestion mapEntity(StackOverflowQuestionEntity entity) {
        return new StackOverflowQuestion(
                entity.getId(),
                entity.getQuestionId(),
                entity.getUpdatedAt(),
                entity.getCreatedAt(),
                entity.getAnswersUpdatedAt()
        );
    }
}
