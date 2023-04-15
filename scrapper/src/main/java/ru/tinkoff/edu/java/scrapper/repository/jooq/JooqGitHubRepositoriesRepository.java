package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.domain.jooq.tables.records.GithubRepositoriesRecord;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.GITHUB_REPOSITORIES;
import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.LINKS;

@Repository
@RequiredArgsConstructor
public class JooqGitHubRepositoriesRepository implements
        BaseRepository<GitHubRepository, GitHubRepositoryAddParams> {
    private final DSLContext create;

    @Override
    public GitHubRepository add(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        return create.insertInto(GITHUB_REPOSITORIES)
                .set(GITHUB_REPOSITORIES.NAME, gitHubRepositoryAddParams.name())
                .set(GITHUB_REPOSITORIES.USERNAME, gitHubRepositoryAddParams.username())
                .returning()
                .fetchOneInto(GitHubRepository.class);
    }

    public GitHubRepository add(GitHubParserResult gitHubParserResult) {
        return add(new GitHubRepositoryAddParams(gitHubParserResult.userName(), gitHubParserResult.projectName()));
    }

    public Optional<GitHubRepository> find(GitHubParserResult gitHubParserResult) {
        return create
                .selectFrom(GITHUB_REPOSITORIES)
                .where(GITHUB_REPOSITORIES.NAME.eq(gitHubParserResult.projectName())
                        .and(GITHUB_REPOSITORIES.USERNAME.eq(gitHubParserResult.userName())))
                .fetchOptionalInto(GitHubRepository.class);
    }

    @Override
    public List<GitHubRepository> findAll() {
        return create.selectFrom(GITHUB_REPOSITORIES).fetchInto(GitHubRepository.class);
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(GITHUB_REPOSITORIES).where(GITHUB_REPOSITORIES.ID.eq(id)).execute();
    }

    public <T> void update(
            List<GitHubRepository> repositories,
            TableField<GithubRepositoriesRecord, T> column,
            T value
    ) {
        var ids = repositories.stream().map(GitHubRepository::id).toList();
        create
                .update(GITHUB_REPOSITORIES)
                .set(column, value)
                .where(GITHUB_REPOSITORIES.ID.in(ids))
                .execute();
    }

    public List<GitHubRepository> findAllWithLinks(int first, TableField<GithubRepositoriesRecord, ?> orderColumn) {
        return create
                .select(GITHUB_REPOSITORIES.asterisk())
                .from(LINKS.join(GITHUB_REPOSITORIES).on(LINKS.GITHUB_REPOSITORY_ID.eq(GITHUB_REPOSITORIES.ID)))
                .orderBy(orderColumn.asc().nullsFirst())
                .limit(first)
                .fetchInto(GitHubRepository.class);
    }

    public void update(GitHubRepository repository, Consumer<GithubRepositoriesRecord> setter) {
        create
                .selectFrom(GITHUB_REPOSITORIES)
                .where(GITHUB_REPOSITORIES.ID.eq(repository.id()))
                .fetchOptional()
                .ifPresent(r -> {
                    setter.accept(r);
                    r.store();
                });
    }
}
