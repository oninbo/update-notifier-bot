package ru.tinkoff.edu.java.link_parser;

import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LinkParserService {
    private final List<LinkParser> parsers;

    public LinkParserService(List<LinkParser> parsers) {
        this.parsers = parsers;
    }

    public Optional<LinkParserResult> parse(URI link) {
        return parsers.stream()
                .map((LinkParser p) -> p.parse(link))
                .filter(Objects::nonNull)
                .findFirst();
    }
}
