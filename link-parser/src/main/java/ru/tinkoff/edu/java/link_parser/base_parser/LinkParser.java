package ru.tinkoff.edu.java.link_parser.base_parser;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class LinkParser {
    public abstract LinkParserResult parse(String link);
    protected URL getURL(String link) {
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
