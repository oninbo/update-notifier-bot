package ru.tinkoff.edu.java.scrapper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
                        rs -> {
                            rs.next();
                            return extractTgChat(rs);
                        },
                        addParams.chatId()
                );
    }

    public Optional<TgChat> find(Long chatId) {
        var result = jdbcTemplate.query(
                "SELECT * FROM tg_chats WHERE chat_id = ?",
                rowMapper(),
                chatId
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
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
        return (ResultSet rs, int rowNum) -> extractTgChat(rs);
    }

    private TgChat extractTgChat(ResultSet rs) throws SQLException {
        return new TgChat(
                rs.getObject("id", UUID.class),
                rs.getLong("chat_id")
        );
    }
}
