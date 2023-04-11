package ru.tinkoff.edu.java.link_parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.configuration.LinkParserConfig;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LinkParserServiceTest {
    private static LinkParserService service;

    @BeforeAll
    public static void initialize() {
        var context = new AnnotationConfigApplicationContext(LinkParserConfig.class);
        service = context.getBean(LinkParserService.class);
    }

    @Test
    public void shouldParseGitHubLink() throws URISyntaxException {
        var link = new URI("https://github.com/sanyarnd/tinkoff-java-course-2022");
        var result = new GitHubParserResult("sanyarnd", "tinkoff-java-course-2022");

        assertEquals(service.parse(link), Optional.of(result));
    }

    @Test
    public void shouldParseStackOverflowLink() throws URISyntaxException {
        var link = new URI(
                "https://stackoverflow.com/questions/42307687/get-complete-jdk-source-code-in-intellij-or-other-ide"
        );
        var result = new StackOverflowParserResult(42307687L);

        assertEquals(service.parse(link), Optional.of(result));
    }

    @Test
    public void shouldNotParseOtherLink() throws URISyntaxException {
        var link = new URI("https://habr.com/ru/post/512730/");

        assertTrue(service.parse(link).isEmpty());
    }
}
