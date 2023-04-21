package ru.tinkoff.edu.java.scrapper.repository.jpa;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
public class JpaTgChatsRepositoryTest extends JpaRepositoryTest {
    @Autowired
    JpaTgChatsRepository jpaTgChatsRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Random(type = Long.class)
    List<Long> chatIds;

    @Test
    @Transactional
    @Rollback
    public void shouldAddTgChat() {
        var chatId = chatIds.get(0);
        var tgChat = jpaTgChatsRepository.add(new TgChatAddParams(chatId));
        assertEquals(chatId, tgChat.getChatId());

        entityManager.flush();
        var addedChatId = jdbcTemplate.queryForObject("SELECT chat_id from tg_chats", Long.class);
        assertEquals(chatId, addedChatId);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllTgChats() {
        for (var charId : chatIds) {
            jdbcTemplate.update("INSERT INTO tg_chats (chat_id) VALUES (?) ON CONFLICT DO NOTHING", charId);
        }
        var foundChatIds = jpaTgChatsRepository.findAll().stream().map(TgChatEntity::getChatId).sorted().toList();
        var expectedChatIds = chatIds.stream().sorted().distinct().toList();
        assertIterableEquals(expectedChatIds, foundChatIds);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindTgChatByChatId() {
        for (var charId : chatIds) {
            jdbcTemplate.update("INSERT INTO tg_chats (chat_id) VALUES (?) ON CONFLICT DO NOTHING", charId);
        }
        var chatId = chatIds.get(0);
        var tgChatResult = jpaTgChatsRepository.find(chatId);
        assertTrue(tgChatResult.isPresent());
        assertEquals(chatId, tgChatResult.get().chatId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveTgChat() {
        var id = jdbcTemplate.queryForObject(
                "INSERT INTO tg_chats (chat_id) VALUES (?) returning id",
                UUID.class,
                chatIds.get(0)
        );
        assertNotNull(id);
        jpaTgChatsRepository.deleteById(id);
        entityManager.flush();

        var ids = jdbcTemplate.queryForList("SELECT id from tg_chats where id = ?", UUID.class, id);
        assertTrue(ids.isEmpty());
    }
}
