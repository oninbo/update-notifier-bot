package ru.tinkoff.edu.java.link_parser;

import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

public interface LinkParserResultVisitor {
    void visit(GitHubParserResult gitHubParserResult);
    void visit(StackOverflowParserResult stackOverflowParserResult);
}
