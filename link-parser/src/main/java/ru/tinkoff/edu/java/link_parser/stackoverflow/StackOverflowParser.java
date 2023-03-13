package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.util.List;

@Component
public class StackOverflowParser extends LinkParser {
    private String stackOverflowHost;

    @Value("${stackoverflow.host}")
    public void setStackOverflowHost(String stackOverflowHost) {
        this.stackOverflowHost = stackOverflowHost;
    }

    @Override
    protected String getHost() {
        return stackOverflowHost;
    }

    @Override
    protected StackOverflowParserResult createResult(String path) {
        List<String> segments = getURLPathSegments(path);
        return new StackOverflowParserResult(segments.get(1));
    }

    @Override
    protected boolean isPathValid(String path) {
        List<String> segments = getURLPathSegments(path);
        return segments.size() >= 2 && segments.get(0).equals("questions");
    }
}
