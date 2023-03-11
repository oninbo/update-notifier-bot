package ru.tinkoff.edu.java.link_parser.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.github.GitHubParser;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParser;

@Configuration
// Если переименовать link-parser.properties в application.properties, по аналогии с bot и scrapper,
// то в BotApplication будет читаться конфиг из bot, а там нет нужных значений
@PropertySource("classpath:/link-parser.properties")
// Почему не @ComponentScan см. BotApplication
public class ApplicationConfig {
    @Bean
    public LinkParserService getLinkParserService() {
        return new LinkParserService();
    }

    @Bean
    public GitHubParser getGitHubParser() {
        return new GitHubParser();
    }

    @Bean
    public StackOverflowParser getStackOverflowParser() {
        return new StackOverflowParser();
    }
}
