package ru.tinkoff.edu.java.link_parser.github;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserWrongURLException;

import static org.junit.Assert.*;

public class GitHubParserTest {
    private GitHubParser parser;

    @Before
    public void initialize() {
        var context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        parser = context.getBean(GitHubParser.class);
    }

    @Test
    public void shouldParseCorrectLinks() {
        String[] correctLinks = {
                "https://github.com/sanyarnd/tinkoff-java-course-2022",
                "https://github.com/sanyarnd/tinkoff-java-course-2022/"
        };
        var expectedResult = new GitHubParserResult("sanyarnd", "tinkoff-java-course-2022");
        // FIXME: use parametrized tests
        for (var link : correctLinks) {
            assertEquals(parser.parse(link), expectedResult);
        }
    }

    @Test
    public void shouldThrowExceptionOnWrongLinks() {
        var wrongLink =
                "https://github.com/sanyarnd/tinkoff-java-course-2022/commit/5ad87f7f9041a4c4fc453cd77e362feda1ce89c9";
        assertThrows(LinkParserWrongURLException.class, () -> parser.parse(wrongLink));
    }

    @Test
    public void shouldReturnNullOnNotGitHubLink() {
        var notGitHubLink = "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c";
        assertNull(parser.parse(notGitHubLink));
    }
}
