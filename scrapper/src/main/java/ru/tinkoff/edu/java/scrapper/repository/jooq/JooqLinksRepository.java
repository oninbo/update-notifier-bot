package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableOnConditionStep;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.*;

@Repository
@RequiredArgsConstructor
public class JooqLinksRepository implements BaseRepository<Link, LinkAddParams> {
    private final DSLContext create;

    @Override
    public Link add(LinkAddParams linkAddParams) {
        return create.insertInto(LINKS)
                .set(LINKS.URL, linkAddParams.url().toString())
                .set(LINKS.TG_CHAT_ID, linkAddParams.tgChatId())
                .set(LINKS.GITHUB_REPOSITORY_ID, linkAddParams.githubRepositoryId())
                .set(LINKS.STACKOVERFLOW_QUESTION_ID, linkAddParams.stackoverflowQuestionId())
                .returning()
                .fetchOneInto(Link.class);
    }

    @Override
    public List<Link> findAll() {
        return create.selectFrom(LINKS).fetchInto(Link.class);
    }

    public List<Link> findAll(Long chatId) {
        return create
                .selectFrom(linksJoinTgChats())
                .where(TG_CHATS.CHAT_ID.eq(chatId))
                .fetchInto(Link.class);
    }

    public Optional<Link> find(TgChat tgChat, GitHubRepository gitHubRepository) {
        var record = create
                .selectFrom(LINKS)
                .where(
                        LINKS.GITHUB_REPOSITORY_ID.eq(gitHubRepository.id())
                                .and(LINKS.TG_CHAT_ID.eq(tgChat.id()))
                )
                .fetchOne();

        return Optional.ofNullable(record).map(r -> r.into(Link.class));
    }

    public Optional<Link> find(TgChat tgChat, StackOverflowQuestion stackOverflowQuestion) {
        var record = create
                .selectFrom(LINKS)
                .where(
                        LINKS.STACKOVERFLOW_QUESTION_ID.eq(stackOverflowQuestion.id())
                                .and(LINKS.TG_CHAT_ID.eq(tgChat.id()))
                )
                .fetchOne();

        return Optional.ofNullable(record).map(r -> r.into(Link.class));
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(LINKS).where(LINKS.ID.eq(id)).execute();
    }

    public List<LinkWithChatId> findAllWithChatId(StackOverflowQuestion stackOverflowQuestion) {
        return create
                .select(LINKS.asterisk(), TG_CHATS.CHAT_ID)
                .from(linksJoinTgChats())
                .where(LINKS.STACKOVERFLOW_QUESTION_ID.eq(stackOverflowQuestion.id()))
                .fetchInto(LinkWithChatId.class);
    }

    private TableOnConditionStep<Record> linksJoinTgChats() {
        return LINKS.join(TG_CHATS)
                .on(TG_CHATS.ID.eq(LINKS.TG_CHAT_ID));
    }
}
