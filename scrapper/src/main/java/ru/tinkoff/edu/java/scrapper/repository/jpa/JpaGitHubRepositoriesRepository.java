package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepositoryAddParams;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGitHubRepositoriesRepository extends JpaRepository<GitHubRepositoryEntity, UUID> {
    default GitHubRepository add(GitHubRepositoryAddParams addParams) {
        var entity = new GitHubRepositoryEntity();
        entity.setName(addParams.name());
        entity.setUsername(addParams.username());
        save(entity);
        return mapEntity(entity);
    }

    default List<GitHubRepository> findAllRepositories() {
        return findAll().stream().map(this::mapEntity).toList();
    }

    default GitHubRepository mapEntity(GitHubRepositoryEntity entity) {
        return new GitHubRepository(
                entity.getId(),
                entity.getUsername(),
                entity.getName(),
                entity.getUpdatedAt(),
                entity.getCreatedAt(),
                entity.getIssuesUpdatedAt()
        );
    }

    @Query("SELECT r FROM GitHubRepositoryEntity AS r JOIN LinkEntity l ON l.gitHubRepository = r")
    List<GitHubRepositoryEntity> findAllWithLinks(Pageable pageable);

    default List<GitHubRepository> findAllWithLinks(int limit, OrderColumn orderColumn) {
        return findAllWithLinks(pageableSortedBy(limit, orderColumn))
                .stream().map(this::mapEntity).toList();
    }

    Optional<GitHubRepositoryEntity> findByUsernameAndName(String username, String name);

    default Optional<GitHubRepository> find(String username, String name) {
        return findByUsernameAndName(username, name).map(this::mapEntity);
    }

    static Pageable pageableSortedBy(int limit, OrderColumn orderColumn) {
        return PageRequest.ofSize(limit)
                .withSort(Sort.by(Sort.Order.by(orderColumn.name())).ascending());
    }

    enum OrderColumn {
        updatedAt,
        issuesUpdatedAt
    }
}
