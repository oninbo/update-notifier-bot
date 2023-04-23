package ru.tinkoff.edu.java.bot.utils;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

@RequiredArgsConstructor
public class LinkParseResultPresenter {
    private final StringBuilder stringBuilder;
    private final String link;
    private final Visitor visitor = new Visitor();

    public void present(LinkParserResult linkParserResult) {
        linkParserResult.acceptVisitor(visitor);
    }

    class Visitor implements LinkParserResultVisitor {
        @Override
        public void visit(GitHubParserResult gitHubParserResult) {
            stringBuilder.append(
                    String.format(
                            "GitHub [репозиторий %s](%s) пользователя %s",
                            gitHubParserResult.projectName(),
                            link,
                            gitHubParserResult.userName()
                    )
            );
        }

        @Override
        public void visit(StackOverflowParserResult stackOverflowParserResult) {
            stringBuilder.append(String.format("[Вопрос](%s) на Stack Overflow", link));
        }
    }
}
