package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGitHubRepositoriesRepository extends JpaRepository<GitHubRepositoryEntity, UUID> {
    default GitHubRepositoryEntity add(GitHubRepositoryAddParams addParams) {
        var entity = new GitHubRepositoryEntity();
        entity.setName(addParams.name());
        entity.setUsername(addParams.username());
        return save(entity);
    }

    @Query("SELECT r FROM GitHubRepositoryEntity AS r JOIN LinkEntity l ON l.gitHubRepository = r")
    List<GitHubRepositoryEntity> findAllWithLinks(Pageable pageable);

    default List<GitHubRepositoryEntity> findAllWithLinks(int limit, OrderColumn orderColumn) {
        return findAllWithLinks(PageRequest.ofSize(limit)
                .withSort(Sort.by(Sort.Order.by(orderColumn.name())).ascending()));
    }

    Optional<GitHubRepositoryEntity> findByUsernameAndName(String username, String name);

    default Optional<GitHubRepositoryEntity> find(GitHubParserResult findParams) {
        return findByUsernameAndName(findParams.userName(), findParams.projectName());
    }

    enum OrderColumn {
        updatedAt,
        issuesUpdatedAt
    }
}
