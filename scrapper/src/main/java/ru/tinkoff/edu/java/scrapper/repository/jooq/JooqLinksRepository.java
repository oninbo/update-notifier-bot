package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.LinkAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.LINKS;

@Repository
@RequiredArgsConstructor
public class JooqLinksRepository implements BaseRepository<Link, LinkAddParams> {
    private final DSLContext create;

    @Override
    public Link add(LinkAddParams linkAddParams) {
        var result = create.insertInto(LINKS)
                .set(LINKS.URL, linkAddParams.url().toString())
                .set(LINKS.TG_CHAT_ID, linkAddParams.tgChatId())
                .set(LINKS.GITHUB_REPOSITORY_ID, linkAddParams.githubRepositoryId())
                .set(LINKS.STACKOVERFLOW_QUESTION_ID, linkAddParams.stackoverflowQuestionId())
                .returning()
                .fetchOne();

        //noinspection DataFlowIssue
        return result.into(Link.class);
    }

    @Override
    public List<Link> findAll() {
        return create.selectFrom(LINKS).fetchInto(Link.class);
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(LINKS).where(LINKS.ID.eq(id)).execute();
    }
}
