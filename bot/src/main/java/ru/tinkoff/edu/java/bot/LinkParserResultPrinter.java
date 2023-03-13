package ru.tinkoff.edu.java.bot;

import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

@Component
public class LinkParserResultPrinter implements LinkParserResultVisitor {
    public void print(LinkParserResult result) {
        result.acceptVisitor(this);
    }

    @Override
    public void visit(GitHubParserResult gitHubParserResult) {
        System.out.println("userName: " + gitHubParserResult.userName() +
                "; projectName: " + gitHubParserResult.projectName());
    }

    @Override
    public void visit(StackOverflowParserResult stackOverflowParserResult) {
        System.out.println("questionId: " + stackOverflowParserResult.questionId());
    }
}
