package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserIncorrectURIException;
import ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig;

import static org.junit.jupiter.api.Assertions.*;

public class StackOverflowParserTest {
    private static StackOverflowParser parser;

    @BeforeAll
    public static void initialize() {
        var context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        parser = context.getBean(StackOverflowParser.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c",
        "https://stackoverflow.com/questions/1642028/"
    })
    public void shouldParseCorrectLinks(String correctLink) {
        var expectedResult = new StackOverflowParserResult("1642028");
        assertEquals(parser.parse(correctLink), expectedResult);
    }

    @Test
    public void shouldThrowExceptionOnWrongLinks() {
        var wrongLink =
                "https://stackoverflow.com/search?q=unsupported%20link";
        assertThrows(LinkParserIncorrectURIException.class, () -> parser.parse(wrongLink));
    }

    @Test
    public void shouldReturnNullOnNotStackOverflowLink() {
        var notStackOverflowLink = "https://github.com/sanyarnd/tinkoff-java-course-2022/";
        assertNull(parser.parse(notStackOverflowLink));
    }
}
