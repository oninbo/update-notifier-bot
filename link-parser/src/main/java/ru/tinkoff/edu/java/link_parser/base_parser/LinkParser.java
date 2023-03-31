package ru.tinkoff.edu.java.link_parser.base_parser;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public abstract class LinkParser {
    public LinkParserResult parse(String link) {
        return parse(getURI(link));
    }

    public LinkParserResult parse(URI link) {
        LinkParserResult result = null;
        if (isURISupported(link)) {
            if (canTakeDataFromURI(link)) {
                result = createResult(link);
            } else {
                throw new LinkParserIncorrectURIException();
            }
        }
        return result;
    }

    protected final URI getURI(String link) {
        try {
            return new URI(link);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected final List<String> getURIPathSegments(String path) {
        return Arrays.stream(path.split("/")).filter((String s) -> !s.isBlank()).toList();
    }

    protected abstract boolean isURISupported(URI uri);

    protected abstract boolean canTakeDataFromURI(URI uri);

    protected abstract LinkParserResult createResult(URI uri);
}
