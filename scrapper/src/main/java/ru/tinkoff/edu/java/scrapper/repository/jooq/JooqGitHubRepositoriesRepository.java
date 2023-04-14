package ru.tinkoff.edu.java.scrapper.repository.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.repository.BaseRepository;

import java.util.List;
import java.util.UUID;

import static ru.tinkoff.edu.java.scrapper.domain.jooq.Tables.GITHUB_REPOSITORIES;

@Repository
@RequiredArgsConstructor
public class JooqGitHubRepositoriesRepository implements
        BaseRepository<GitHubRepository, GitHubRepositoryAddParams> {
    private final DSLContext create;

    @Override
    public GitHubRepository add(GitHubRepositoryAddParams gitHubRepositoryAddParams) {
        var result = create.insertInto(GITHUB_REPOSITORIES)
                .set(GITHUB_REPOSITORIES.NAME, gitHubRepositoryAddParams.name())
                .set(GITHUB_REPOSITORIES.USERNAME, gitHubRepositoryAddParams.username())
                .returning()
                .fetchOne();
        //noinspection DataFlowIssue
        return result.into(GitHubRepository.class);
    }

    @Override
    public List<GitHubRepository> findAll() {
        return create.selectFrom(GITHUB_REPOSITORIES).fetchInto(GitHubRepository.class);
    }

    @Override
    public void remove(UUID id) {
        create.deleteFrom(GITHUB_REPOSITORIES).where(GITHUB_REPOSITORIES.ID.eq(id)).execute();
    }
}
