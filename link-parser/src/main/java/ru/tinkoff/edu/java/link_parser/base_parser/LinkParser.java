package ru.tinkoff.edu.java.link_parser.base_parser;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public abstract class LinkParser {
    public LinkParserResult parse(String link) {
        URL url = getURL(link);
        LinkParserResult result = null;
        if (isURLSupported(url)) {
            if (canTakeDataFromURL(url)) {
                result = createResult(url);
            } else {
                throw new LinkParserIncorrectURLException();
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

    protected abstract boolean isURLSupported(URL url);

    protected abstract boolean canTakeDataFromURL(URL url);

    protected abstract LinkParserResult createResult(URL url);
}
