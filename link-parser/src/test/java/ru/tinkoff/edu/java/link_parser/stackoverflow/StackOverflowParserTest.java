package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectLinkException;
import ru.tinkoff.edu.java.link_parser.configuration.LinkParserConfig;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class StackOverflowParserTest {
    private static StackOverflowParser parser;

    @BeforeAll
    public static void initialize() {
        var context = new AnnotationConfigApplicationContext(LinkParserConfig.class);
        parser = context.getBean(StackOverflowParser.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c",
        "https://stackoverflow.com/questions/1642028/"
    })
    public void shouldParseCorrectLinks(String correctLink) throws URISyntaxException {
        var expectedResult = new StackOverflowParserResult(1642028L);
        assertEquals(parser.parse(new URI(correctLink)), expectedResult);
    }

    @Test
    public void shouldThrowExceptionOnWrongLinks() throws URISyntaxException {
        var wrongLink = new URI("https://stackoverflow.com/search?q=unsupported%20link");
        assertThrows(LinkParserIncorrectLinkException.class, () -> parser.parse(wrongLink));
    }

    @Test
    public void shouldReturnNullOnNotStackOverflowLink() throws URISyntaxException {
        var notStackOverflowLink = new URI("https://github.com/sanyarnd/tinkoff-java-course-2022/");
        assertNull(parser.parse(notStackOverflowLink));
    }
}
