package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;

@RequiredArgsConstructor
class LinkParseResultPresenter implements LinkParserResultVisitor {
    private final StringBuilder stringBuilder;
    private final String link;

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
