package ru.tinkoff.edu.java.link_parser.base_parser;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class LinkParser {
    private final List<String> supportedURISchemas = List.of("http", "https");

    public LinkParserResult parse(URI link) {
        checkURI(link);
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

    protected boolean isURISupported(URI uri) {
        return supportedURISchemas.stream().anyMatch(s -> s.equals(uri.getScheme()));
    }

    protected abstract boolean canTakeDataFromURI(URI uri);

    protected abstract LinkParserResult createResult(URI uri);

    private void checkURI(URI uri) {
        if (Stream.of(uri.getHost(), uri.getPath()).anyMatch(Objects::isNull)) {
            throw new LinkParserIncorrectURIException();
        }
    }
}
