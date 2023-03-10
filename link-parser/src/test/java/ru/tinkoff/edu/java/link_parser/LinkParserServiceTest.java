package ru.tinkoff.edu.java.link_parser;

import org.junit.Before;
import org.junit.Test;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;
import ru.tinkoff.edu.java.link_parser.github.GitHubParser;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParser;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class LinkParserServiceTest {
    private LinkParserService service;

    @Before
    public void initialize() {
        service = new LinkParserService();
        var parsers = new ArrayList<LinkParser>() {{
            add(new StackOverflowParser());
            add(new GitHubParser());
        }};
        service.setParsers(parsers);
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
}
