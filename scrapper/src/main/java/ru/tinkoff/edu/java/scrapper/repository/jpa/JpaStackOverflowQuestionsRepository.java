package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestionAddParams;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;
import ru.tinkoff.edu.java.scrapper.mapper.StackOverflowQuestionMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStackOverflowQuestionsRepository extends JpaRepository<StackOverflowQuestionEntity, UUID> {
    default StackOverflowQuestion add(StackOverflowQuestionAddParams addParams, StackOverflowQuestionMapper mapper) {
        return mapper.fromEntity(add(addParams));
    }

    default StackOverflowQuestionEntity add(StackOverflowQuestionAddParams addParams) {
        var entity = new StackOverflowQuestionEntity();
        entity.setQuestionId(addParams.questionId());
        return save(entity);
    }

    Optional<StackOverflowQuestionEntity> findByQuestionId(Long questionId);

    @Query("SELECT sq FROM StackOverflowQuestionEntity AS sq JOIN LinkEntity AS l")
    List<StackOverflowQuestionEntity> findWithLinks(Pageable pageable);

    default List<StackOverflowQuestion> findWithLinks(
            int first,
            OrderColumn orderColumn,
            StackOverflowQuestionMapper mapper
    ) {
        return findWithLinks(PageRequest.ofSize(first)
                .withSort(Sort.by(Sort.Order.by(orderColumn.name())).ascending()))
                .stream().map(mapper::fromEntity)
                .toList();
    }

    enum OrderColumn {
        updatedAt,
        answersUpdatedAt
    }
}
