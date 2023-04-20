package ru.tinkoff.edu.java.scrapper.configuration;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.mapper.GithubRepositoryMapper;

@Configuration
public class MapperConfig {
    @Bean
    public GithubRepositoryMapper githubRepositoryMapper() {
        return Mappers.getMapper(GithubRepositoryMapper.class);
    }
}
