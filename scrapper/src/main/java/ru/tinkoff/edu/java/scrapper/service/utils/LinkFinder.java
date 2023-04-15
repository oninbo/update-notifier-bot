package ru.tinkoff.edu.java.scrapper.service.utils;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.dto.GitHubRepository;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowQuestion;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class LinkFinder {
    private final Function<GitHubRepository, Optional<Link>> findGitHubRepositoryLink;
    private final Function<StackOverflowQuestion, Optional<Link>> findStackOverflowQuestionLink;
    private final FindOrDoService<StackOverflowQuestion, StackOverflowParserResult> stackOverflowQuestionsService;
    private final FindOrDoService<GitHubRepository, GitHubParserResult> gitHubRepositoriesService;
    private Supplier<Optional<Link>> onFind;

    public Optional<Link> find(LinkParserResult linkParserResult) {
        linkParserResult.acceptVisitor(new Visitor());
        return onFind.get();
    }

    class Visitor implements LinkParserResultVisitor {
        @Override
        public void visit(GitHubParserResult gitHubParserResult) {
            onFind = () -> findGitHubRepositoryLink.apply(gitHubRepositoriesService.findOrThrow(gitHubParserResult));
        }

        @Override
        public void visit(StackOverflowParserResult stackOverflowParserResult) {
            onFind = () -> findStackOverflowQuestionLink
                    .apply(stackOverflowQuestionsService.findOrThrow(stackOverflowParserResult));
        }
    }
}
