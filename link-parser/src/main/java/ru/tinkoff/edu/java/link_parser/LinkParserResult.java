package ru.tinkoff.edu.java.link_parser;

public interface LinkParserResult {
    void acceptVisitor(LinkParserResultVisitor visitor);
}
