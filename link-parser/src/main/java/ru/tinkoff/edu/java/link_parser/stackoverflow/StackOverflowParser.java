package ru.tinkoff.edu.java.link_parser.stackoverflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class StackOverflowParser extends LinkParser {
    private final String stackOverflowHost;
    private final Pattern questionIdPattern = Pattern.compile("\\d+");

    public StackOverflowParser(@Value("${stackoverflow.host}") String stackOverflowHost) {
        this.stackOverflowHost = stackOverflowHost;
    }

    @Override
    protected StackOverflowParserResult createResult(URI uri) {
        List<String> segments = getURIPathSegments(uri);
        return new StackOverflowParserResult(Long.parseLong(segments.get(1)));
    }

    @Override
    protected boolean isURISupported(URI uri) {
        return super.isURISupported(uri) && getURIHost(uri).equals(stackOverflowHost);
    }

    @Override
    protected boolean canTakeDataFromURI(URI uri) {
        List<String> segments = getURIPathSegments(uri);
        if (!(segments.size() >= 2 && segments.get(0).equals("questions"))) {
            return false;
        }
        return questionIdPattern.matcher(segments.get(1)).matches();
    }
}
