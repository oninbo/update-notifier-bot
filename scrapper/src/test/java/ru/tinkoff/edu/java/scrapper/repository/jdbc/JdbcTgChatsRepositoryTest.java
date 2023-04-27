package ru.tinkoff.edu.java.scrapper.repository.jdbc;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.configuration.JdbcConfig;
import ru.tinkoff.edu.java.scrapper.configuration.TestDataSourceConfig;
import ru.tinkoff.edu.java.scrapper.configuration.TransactionConfig;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TransactionConfig.class,
        JdbcTgChatsRepository.class,
        TestDataSourceConfig.class,
        JdbcConfig.class
})
@ExtendWith(RandomBeansExtension.class)
public class JdbcTgChatsRepositoryTest {
    @Autowired
    JdbcTgChatsRepository jdbcTgChatsRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Random(type = Long.class)
    List<Long> chatIds;

    @Test
    @Transactional
    @Rollback
    public void shouldAddTgChat() {
        var chatId = chatIds.get(0);
        var tgChat = jdbcTgChatsRepository.add(new TgChatAddParams(chatId));
        assertEquals(chatId, tgChat.chatId());

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
        var foundChatIds = jdbcTgChatsRepository.findAll().stream().map(TgChat::chatId).sorted().toList();
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
        var tgChatResult = jdbcTgChatsRepository.find(chatId);
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
        jdbcTgChatsRepository.remove(id);

        var ids = jdbcTemplate.queryForList("SELECT id from tg_chats where id = ?", UUID.class, id);
        assertTrue(ids.isEmpty());
    }
}
