package ru.tinkoff.edu.java.link_parser.base_parser;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class LinkParser {
    public LinkParserResult parse(URI uri) {
        Link link = new Link(uri.getHost(), uri.getPath());

        if (!link.isValid()) {
            throw new LinkParserIncorrectURIException();
        }

        if (isLinkSupported(link)) {
            if (canTakeDataFromLink(link)) {
                return createResult(link);
            } else {
                throw new LinkParserIncorrectURIException();
            }
        }
        return null;
    }

    protected final List<String> getURIPathSegments(String path) {
        return Arrays.stream(path.split("/")).filter((String s) -> !s.isBlank()).toList();
    }

    protected abstract boolean isLinkSupported(Link link);

    protected abstract boolean canTakeDataFromLink(Link link);

    protected abstract LinkParserResult createResult(Link link);

    protected record Link(String host, String path) {
        public boolean isValid() {
            return Stream.of(host, path).noneMatch(Objects::isNull);
        }
    }
}
