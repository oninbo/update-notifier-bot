package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TgChatsRepository implements BaseRepository<TgChat, TgChatAddParams> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public TgChat add(TgChatAddParams addParams) {
        return jdbcTemplate
                .query(
                        "INSERT INTO tg_chats (chat_id) VALUES (?) RETURNING *",
                        resultSetExtractor(),
                        addParams.chatId()
                );
    }

    public TgChat find(Long chatId) {
        var result = jdbcTemplate.query(
                "SELECT * FROM tg_chats WHERE chat_id = ?",
                rowMapper(),
                chatId
        );
        if (result.isEmpty()) {
            throw new TgChatNotFoundException();
        }
        return result.get(0);
    }

    @Override
    public List<TgChat> findAll() {
        return jdbcTemplate.query("SELECT * FROM tg_chats", rowMapper());
    }

    @Override
    public void remove(UUID id) {
        jdbcTemplate.update("DELETE FROM tg_chats WHERE id = ?", id);
    }

    private RowMapper<TgChat> rowMapper() {
        return (ResultSet rs, int rowNum) -> resultSetExtractor().extractData(rs);
    }

    private ResultSetExtractor<TgChat> resultSetExtractor() {
        return (ResultSet rs) -> new TgChat(
                rs.getObject("id", UUID.class),
                rs.getLong("tg_chat")
        );
    }
}
