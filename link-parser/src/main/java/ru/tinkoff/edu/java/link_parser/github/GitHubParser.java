package ru.tinkoff.edu.java.link_parser.github;

import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.util.List;


// TODO: Add component annotation
public class GitHubParser extends LinkParser {
    private final String GITHUB_HOST = "github.com"; // TODO: change to value from properties

    @Override
    protected GitHubParserResult createResult(String path) {
        List<String> segments = getURLPathSegments(path);
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }

    @Override
    protected String getHost() {
        return GITHUB_HOST;
    }

    @Override
    protected boolean isPathValid(String path) {
        return getURLPathSegments(path).size() == 2;
    }
}
