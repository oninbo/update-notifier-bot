package ru.tinkoff.edu.java.link_parser.stackoverflow;

import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.util.List;

// TODO: Add component annotation
public class StackOverflowParser extends LinkParser {
    private final String STACK_OVERFLOW_HOST = "stackoverflow.com"; // TODO: change to value from properties

    @Override
    protected String getHost() {
        return STACK_OVERFLOW_HOST;
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
