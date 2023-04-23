package ru.tinkoff.edu.java.link_parser.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;
import ru.tinkoff.edu.java.link_parser.github.GitHubParser;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParser;

import java.util.List;

@Configuration
// Если переименовать link-parser.properties в application.properties, по аналогии с bot и scrapper,
// то в BotApplication будет читаться конфиг из bot, а там нет нужных значений
@PropertySource("classpath:/link-parser.properties")
public class LinkParserConfig {
    @Bean
    public LinkParserService getLinkParserService(List<LinkParser> parsers) {
        return new LinkParserService(parsers);
    }

    @Bean
    public GitHubParser getGitHubParser(@Value("${github.host}") String gitHubHost) {
        return new GitHubParser(gitHubHost);
    }

    @Bean
    public StackOverflowParser getStackOverflowParser(@Value("${stackoverflow.host}") String stackOverflowHost) {
        return new StackOverflowParser(stackOverflowHost);
    }
}
