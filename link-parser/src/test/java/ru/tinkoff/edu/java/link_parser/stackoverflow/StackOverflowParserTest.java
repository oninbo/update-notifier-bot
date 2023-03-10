package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserWrongURLException;
import ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig;

import static org.junit.Assert.*;

public class StackOverflowParserTest {
    private StackOverflowParser parser;

    @Before
    public void initialize() {
        var context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        parser = context.getBean(StackOverflowParser.class);
    }

    @Test
    public void shouldParseCorrectLinks() {
        String[] correctLinks = {
                "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c",
                "https://stackoverflow.com/questions/1642028/"
        };
        var expectedResult = new StackOverflowParserResult("1642028");
        // FIXME: use parametrized tests
        for (var link : correctLinks) {
            assertEquals(parser.parse(link), expectedResult);
        }
    }

    @Test
    public void shouldThrowExceptionOnWrongLinks() {
        var wrongLink =
                "https://stackoverflow.com/search?q=unsupported%20link";
        assertThrows(LinkParserWrongURLException.class, () -> parser.parse(wrongLink));
    }

    @Test
    public void shouldReturnNullOnNotStackOverflowLink() {
        var notStackOverflowLink = "https://github.com/sanyarnd/tinkoff-java-course-2022/";
        assertNull(parser.parse(notStackOverflowLink));
    }
}
