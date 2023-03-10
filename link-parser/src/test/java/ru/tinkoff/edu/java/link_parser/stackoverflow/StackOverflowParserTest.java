package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.junit.Before;
import org.junit.Test;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserWrongURLException;

import static org.junit.Assert.*;

public class StackOverflowParserTest {
    private StackOverflowParser parser;

    @Before
    public void initialize() {
        parser = new StackOverflowParser();
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
