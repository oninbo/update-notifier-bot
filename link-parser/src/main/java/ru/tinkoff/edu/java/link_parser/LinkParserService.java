package ru.tinkoff.edu.java.link_parser;

import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParserResult;

import java.util.List;
import java.util.Objects;

// TODO: Add service annotation
public class LinkParserService {
    private List<LinkParser> parsers;

    public void setParsers(List<LinkParser> parsers) {
        this.parsers = parsers;
    }

    public LinkParserResult parse(String link) {
        return parsers.stream()
                .map((LinkParser p) -> p.parse(link))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
