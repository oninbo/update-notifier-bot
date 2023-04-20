package ru.tinkoff.edu.java.scrapper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "stackoverflow_questions")
public class StackOverflowQuestionEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "question_id")
    @Setter
    private Long questionId;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @CreationTimestamp
    @Setter
    private OffsetDateTime updatedAt;

    @CreationTimestamp
    @Setter
    @Column(name = "answers_updated_at")
    private OffsetDateTime answersUpdatedAt;
}
