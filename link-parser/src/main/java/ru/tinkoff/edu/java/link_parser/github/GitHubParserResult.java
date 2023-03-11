package ru.tinkoff.edu.java.link_parser.github;

import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;

public record GitHubParserResult(String userName, String projectName) implements LinkParserResult {
    @Override
    public void acceptVisitor(LinkParserResultVisitor visitor) {
        visitor.visit(this);
    }
}
