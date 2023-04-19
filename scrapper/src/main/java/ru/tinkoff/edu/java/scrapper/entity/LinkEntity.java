package ru.tinkoff.edu.java.scrapper.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.net.URI;
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
    private URI url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tg_chat_id")
    private TgChatEntity tgChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="stackoverflow_question_id")
    private StackOverflowQuestionEntity stackOverflowQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="github_repository_id")
    private GitHubRepositoryEntity gitHubRepository;
}
