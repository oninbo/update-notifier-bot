package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URL;
import java.util.List;

@Component
public class StackOverflowParser extends LinkParser {
    private final String stackOverflowHost;

    public StackOverflowParser(@Value("${stackoverflow.host}") String stackOverflowHost) {
        this.stackOverflowHost = stackOverflowHost;
    }

    @Override
    protected StackOverflowParserResult createResult(URL url) {
        List<String> segments = getURLPathSegments(url.getPath());
        return new StackOverflowParserResult(segments.get(1));
    }

    @Override
    protected boolean isURLSupported(URL url) {
        return url.getHost().equals(stackOverflowHost);
    }

    @Override
    protected boolean canTakeDataFromURL(URL url) {
        List<String> segments = getURLPathSegments(url.getPath());
        return segments.size() >= 2 && segments.get(0).equals("questions");
    }
}
