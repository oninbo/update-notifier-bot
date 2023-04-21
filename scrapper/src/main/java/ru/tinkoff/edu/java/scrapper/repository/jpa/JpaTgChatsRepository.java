package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("JpaQlInspection") // Ругается на вызов конструктора DTO в запросах
public interface JpaTgChatsRepository extends JpaRepository<TgChatEntity, UUID> {
    default TgChatEntity add(TgChatAddParams addParams) {
        var entity = new TgChatEntity();
        entity.setChatId(addParams.chatId());
        return save(entity);
    }

    @Query("""
            SELECT new ru.tinkoff.edu.java.scrapper.dto.TgChat(chat.id, chat.chatId)
            FROM TgChatEntity AS chat
            WHERE chat.chatId = :chatId
            """)
    Optional<TgChat> find(@Param("chatId") Long chatId);

    Optional<TgChatEntity> findByChatId(Long chatId);
}
