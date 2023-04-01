package ru.tinkoff.edu.java.link_parser.base_parser;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class LinkParser {
    public LinkParserResult parse(URI link) {
        if (isURISupported(link)) {
            if (canTakeDataFromURI(link)) {
                return createResult(link);
            } else {
                throw new LinkParserIncorrectURIException();
            }
        }
        return null;
    }

    protected final List<String> getURIPathSegments(URI uri) {
        String path = Optional.ofNullable(uri.getPath())
                .orElseThrow(LinkParserIncorrectURIException::new);
        return Arrays.stream(path.split("/")).filter((String s) -> !s.isBlank()).toList();
    }

    protected final String getURIHost(URI uri) {
        return Optional.ofNullable(uri.getHost())
                .orElseThrow(LinkParserIncorrectURIException::new);
    }

    protected abstract boolean isURISupported(URI uri);

    protected abstract boolean canTakeDataFromURI(URI uri);

    protected abstract LinkParserResult createResult(URI uri);
}
