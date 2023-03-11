package ru.tinkoff.edu.java.link_parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.base_parser.LinkParser;

import java.util.List;
import java.util.Objects;

@Service
public class LinkParserService {
    private List<LinkParser> parsers;

    @Autowired
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
