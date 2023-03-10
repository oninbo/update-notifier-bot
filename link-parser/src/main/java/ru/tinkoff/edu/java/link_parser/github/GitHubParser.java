package ru.tinkoff.edu.java.link_parser.github;

import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserWrongURLException;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URL;
import java.util.Arrays;
import java.util.List;


// TODO: Add component annotation
public class GitHubParser extends LinkParser {
    private final String GITHUB_HOST = "github.com"; // TODO: change to value from properties

    @Override
    public GitHubParserResult parse(String link) {
        URL url = getURL(link);
        GitHubParserResult result = null;
        if (url.getHost().equals(GITHUB_HOST)) {
            result = createResult(url.getPath());
        }
        return result;
    }

    private GitHubParserResult createResult(String path) {
        List<String> segments = Arrays.stream(path.split("/")).filter((String s) -> !s.isBlank()).toList();
        if (segments.size() != 2) {
            throw new LinkParserWrongURLException();
        }
        return new GitHubParserResult(segments.get(0), segments.get(1));
    }
}
