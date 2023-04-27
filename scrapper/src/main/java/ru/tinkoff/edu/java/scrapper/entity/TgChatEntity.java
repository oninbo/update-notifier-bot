package ru.tinkoff.edu.java.scrapper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "tg_chats")
public class TgChatEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "chat_id")
    @Setter
    private Long chatId;
}
