package ru.tinkoff.edu.java.link_parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LinkParserServiceTest {
    private static LinkParserService service;

    @BeforeAll
    public static void initialize() {
        var context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        service = context.getBean(LinkParserService.class);
    }

    @Test
    public void shouldParseGitHubLink() {
        var link = "https://github.com/sanyarnd/tinkoff-java-course-2022";
        var result = new GitHubParserResult("sanyarnd", "tinkoff-java-course-2022");

        assertEquals(service.parse(link), result);
    }

    @Test
    public void shouldParseStackOverflowLink() {
        var link = "https://stackoverflow.com/questions/42307687/get-complete-jdk-source-code-in-intellij-or-other-ide";
        var result = new StackOverflowParserResult("42307687");

        assertEquals(service.parse(link), result);
    }

    @Test
    public void shouldNotParseOtherLink() {
        var link = "https://habr.com/ru/post/512730/";

        assertNull(service.parse(link));
    }
}
