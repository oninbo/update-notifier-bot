package ru.tinkoff.edu.java.link_parser.github;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.configuration.LinkParserConfig;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectLinkException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class GitHubParserTest {
    private static GitHubParser parser;

    @BeforeAll
    public static void initialize() {
        var context = new AnnotationConfigApplicationContext(LinkParserConfig.class);
        parser = context.getBean(GitHubParser.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://github.com/sanyarnd/tinkoff-java-course-2022",
        "https://github.com/sanyarnd/tinkoff-java-course-2022/"
    })
    public void shouldParseCorrectLinks(String correctLink) throws URISyntaxException {
        var expectedResult = new GitHubParserResult("sanyarnd", "tinkoff-java-course-2022");
        assertEquals(parser.parse(new URI(correctLink)), expectedResult);
    }

    @Test
    public void shouldThrowExceptionOnWrongLinks() throws URISyntaxException {
        var wrongLink = new URI(
                "https://github.com/sanyarnd/tinkoff-java-course-2022/commit/5ad87f7f9041a4c4fc453cd77e362feda1ce89c9"
        );
        assertThrows(LinkParserIncorrectLinkException.class, () -> parser.parse(wrongLink));
    }

    @Test
    public void shouldReturnNullOnNotGitHubLink() throws URISyntaxException {
        var notGitHubLink = new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c");
        assertNull(parser.parse(notGitHubLink));
    }
}
