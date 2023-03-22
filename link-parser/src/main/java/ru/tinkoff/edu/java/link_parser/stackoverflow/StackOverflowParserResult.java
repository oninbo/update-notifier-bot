package ru.tinkoff.edu.java.link_parser.stackoverflow;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;

public record StackOverflowParserResult(String questionId) implements LinkParserResult {
    @Override
    public void acceptVisitor(LinkParserResultVisitor visitor) {
        visitor.visit(this);
    }
}
