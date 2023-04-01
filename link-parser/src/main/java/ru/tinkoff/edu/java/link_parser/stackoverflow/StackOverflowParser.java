package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URI;
import java.util.List;

@Component
public class StackOverflowParser extends LinkParser {
    private final String stackOverflowHost;

    public StackOverflowParser(@Value("${stackoverflow.host}") String stackOverflowHost) {
        this.stackOverflowHost = stackOverflowHost;
    }

    @Override
    protected StackOverflowParserResult createResult(URI uri) {
        List<String> segments = getURIPathSegments(uri);
        return new StackOverflowParserResult(segments.get(1));
    }

    @Override
    protected boolean isURISupported(URI uri) {
        return super.isURISupported(uri) && getURIHost(uri).equals(stackOverflowHost);
    }

    @Override
    protected boolean canTakeDataFromURI(URI uri) {
        List<String> segments = getURIPathSegments(uri);
        return segments.size() >= 2 && segments.get(0).equals("questions");
    }
}
