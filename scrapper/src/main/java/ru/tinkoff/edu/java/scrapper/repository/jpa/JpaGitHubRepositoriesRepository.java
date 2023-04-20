package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.mapper.GithubRepositoryMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGitHubRepositoriesRepository extends JpaRepository<GitHubRepositoryEntity, UUID> {
    default GitHubRepository add(GitHubRepositoryAddParams addParams, GithubRepositoryMapper mapper) {
        return mapper.fromEntity(add(addParams));
    }

    default GitHubRepositoryEntity add(GitHubRepositoryAddParams addParams) {
        var entity = new GitHubRepositoryEntity();
        entity.setName(addParams.name());
        entity.setUsername(addParams.username());
        return save(entity);
    }

    @Query("SELECT r FROM GitHubRepositoryEntity AS r JOIN LinkEntity l ON l.gitHubRepository = r")
    List<GitHubRepositoryEntity> findAllWithLinks(Pageable pageable);

    default List<GitHubRepository> findAllWithLinks(int limit, OrderColumn orderColumn, GithubRepositoryMapper mapper) {
        return findAllWithLinks(pageableSortedBy(limit, orderColumn))
                .stream().map(mapper::fromEntity).toList();
    }

    Optional<GitHubRepositoryEntity> findByUsernameAndName(String username, String name);

    default Optional<GitHubRepository> find(String username, String name, GithubRepositoryMapper mapper) {
        return findByUsernameAndName(username, name).map(mapper::fromEntity);
    }

    static Pageable pageableSortedBy(int limit, OrderColumn orderColumn) {
        return PageRequest.ofSize(limit)
                .withSort(Sort.by(Sort.Order.by(orderColumn.name())).ascending());
    }

    default Optional<GitHubRepository> find(GitHubParserResult findParams, GithubRepositoryMapper mapper) {
        return find(findParams.userName(), findParams.projectName(), mapper);
    }

    enum OrderColumn {
        updatedAt,
        issuesUpdatedAt
    }
}
