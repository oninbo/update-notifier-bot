package ru.tinkoff.edu.java.scrapper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.tinkoff.edu.java.scrapper.converter.UriPersistenceConverter;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "links")
public class LinkEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "url")
    @Setter
    @Convert(converter = UriPersistenceConverter.class)
    private URI url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tg_chat_id")
    @Setter
    private TgChatEntity tgChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="stackoverflow_question_id")
    @Setter
    private StackOverflowQuestionEntity stackOverflowQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="github_repository_id")
    @Setter
    private GitHubRepositoryEntity gitHubRepository;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;
}
