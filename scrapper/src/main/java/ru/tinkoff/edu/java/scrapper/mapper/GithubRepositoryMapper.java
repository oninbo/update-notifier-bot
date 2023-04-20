package ru.tinkoff.edu.java.scrapper.mapper;

import org.mapstruct.Mapper;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;

@Mapper
public interface GithubRepositoryMapper {
    GitHubRepository fromEntity(GitHubRepositoryEntity entity);
    GitHubRepositoryEntity toEntity(GitHubRepository gitHubRepository);
}
