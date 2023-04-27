package ru.tinkoff.edu.java.scrapper.configuration;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.mapper.GithubRepositoryMapper;
import ru.tinkoff.edu.java.scrapper.mapper.LinkMapper;
import ru.tinkoff.edu.java.scrapper.mapper.StackOverflowQuestionMapper;

@Configuration
public class MapperConfig {
    @Bean
    public GithubRepositoryMapper githubRepositoryMapper() {
        return Mappers.getMapper(GithubRepositoryMapper.class);
    }

    @Bean
    public StackOverflowQuestionMapper stackOverflowQuestionMapper() {
        return Mappers.getMapper(StackOverflowQuestionMapper.class);
    }

    @Bean
    public LinkMapper linkMapper() {
        return Mappers.getMapper(LinkMapper.class);
    }
}
