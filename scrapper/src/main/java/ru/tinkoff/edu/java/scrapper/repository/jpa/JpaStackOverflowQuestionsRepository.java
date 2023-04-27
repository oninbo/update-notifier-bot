package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStackOverflowQuestionsRepository extends JpaRepository<StackOverflowQuestionEntity, UUID> {
    default StackOverflowQuestionEntity add(StackOverflowQuestionAddParams addParams) {
        var entity = new StackOverflowQuestionEntity();
        entity.setQuestionId(addParams.questionId());
        return save(entity);
    }

    Optional<StackOverflowQuestionEntity> findByQuestionId(Long questionId);

    @Query("SELECT sq FROM StackOverflowQuestionEntity AS sq JOIN LinkEntity AS l ON l.stackOverflowQuestion = sq")
    List<StackOverflowQuestionEntity> findAllWithLinks(Pageable pageable);

    default List<StackOverflowQuestionEntity> findAllWithLinks(
            int first,
            OrderColumn orderColumn
    ) {
        return findAllWithLinks(PageRequest.ofSize(first)
                .withSort(Sort.by(Sort.Order.by(orderColumn.name())).ascending()));
    }

    enum OrderColumn {
        updatedAt,
        answersUpdatedAt
    }
}
