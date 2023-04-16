package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.TableOnConditionStep;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.LINKS;
import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.TG_CHATS;

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
                .fetchSingle(recordMapper());
    }

    @Override
    public List<Link> findAll() {
        return create.selectFrom(LINKS).fetchInto(Link.class);
    }

    public List<Link> findAll(Long chatId) {
        return create
                .select(LINKS.asterisk())
                .from(linksJoinTgChats())
                .where(TG_CHATS.CHAT_ID.eq(chatId))
                .fetch(recordMapper());
    }

    public Optional<Link> find(TgChat tgChat, GitHubRepository gitHubRepository) {
        return create
                .selectFrom(LINKS)
                .where(
                        LINKS.GITHUB_REPOSITORY_ID.eq(gitHubRepository.id())
                                .and(LINKS.TG_CHAT_ID.eq(tgChat.id()))
                )
                .fetchOptional(recordMapper());
    }

    public Optional<Link> find(TgChat tgChat, StackOverflowQuestion stackOverflowQuestion) {
        return create
                .selectFrom(LINKS)
                .where(
                        LINKS.STACKOVERFLOW_QUESTION_ID.eq(stackOverflowQuestion.id())
                                .and(LINKS.TG_CHAT_ID.eq(tgChat.id()))
                )
                .fetchOptional(recordMapper());
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(LINKS).where(LINKS.ID.eq(id)).execute();
    }

    public List<LinkWithChatId> findAllWithChatId(StackOverflowQuestion question, OffsetDateTime createdBefore) {
        return create
                .select(LINKS.asterisk(), TG_CHATS.CHAT_ID)
                .from(linksJoinTgChats())
                .where(LINKS.STACKOVERFLOW_QUESTION_ID.eq(question.id())
                        .and(LINKS.CREATED_AT.lessThan(createdBefore)))
                .fetch(recordMapperWithChatId());
    }

    private TableOnConditionStep<Record> linksJoinTgChats() {
        return LINKS.join(TG_CHATS)
                .on(TG_CHATS.ID.eq(LINKS.TG_CHAT_ID));
    }

    public List<LinkWithChatId> findAllWithChatId(GitHubRepository gitHubRepository, OffsetDateTime createdBefore) {
        return create
                .select(LINKS.asterisk(), TG_CHATS.CHAT_ID)
                .from(linksJoinTgChats())
                .where(LINKS.GITHUB_REPOSITORY_ID.eq(gitHubRepository.id())
                        .and(LINKS.CREATED_AT.lessThan(createdBefore)))
                .fetch(recordMapperWithChatId());
    }

    private RecordMapper<Record, Link> recordMapper() {
        return (record) -> new Link(record.get(LINKS.ID), record.get(LINKS.URL, URI.class));
    }

    private RecordMapper<Record, LinkWithChatId> recordMapperWithChatId() {
        return record -> new LinkWithChatId(
                record.get(LINKS.ID),
                record.get(LINKS.URL, URI.class),
                record.get(TG_CHATS.CHAT_ID));
    }
}
