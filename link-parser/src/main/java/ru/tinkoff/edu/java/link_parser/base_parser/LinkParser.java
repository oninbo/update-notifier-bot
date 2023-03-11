package ru.tinkoff.edu.java.link_parser.base_parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public abstract class LinkParser {
    public LinkParserResult parse(String link) {
        URL url = getURL(link);
        LinkParserResult result = null;
        if (url.getHost().equals(getHost())) {
            String path = url.getPath();
            if (isPathValid(path)) {
                result = createResult(path);
            } else {
                throw new LinkParserWrongURLException();
            }
        }
        return result;
    }

    protected final URL getURL(String link) {
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected final List<String> getURLPathSegments(String path) {
        return Arrays.stream(path.split("/")).filter((String s) -> !s.isBlank()).toList();
    }

    protected abstract String getHost();

    protected abstract LinkParserResult createResult(String path);

    protected abstract boolean isPathValid(String path);
}
