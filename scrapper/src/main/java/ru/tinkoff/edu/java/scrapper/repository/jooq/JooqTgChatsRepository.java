package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.TG_CHATS;

@Repository
@RequiredArgsConstructor
public class JooqTgChatsRepository implements BaseRepository<TgChat, TgChatAddParams> {
    private final DSLContext create;

    @Override
    public TgChat add(TgChatAddParams addParams) {
        var result = create.insertInto(TG_CHATS)
                .set(TG_CHATS.CHAT_ID, addParams.chatId())
                .returning()
                .fetchOne();
        //noinspection DataFlowIssue
        return result.into(TgChat.class);
    }

    @Override
    public List<TgChat> findAll() {
        return create.selectFrom(TG_CHATS).fetchInto(TgChat.class);
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(TG_CHATS).where(TG_CHATS.ID.eq(id)).execute();
    }
}
