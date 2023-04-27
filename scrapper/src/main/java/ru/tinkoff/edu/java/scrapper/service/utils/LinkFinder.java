package ru.tinkoff.edu.java.scrapper.service.utils;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class LinkFinder<S, G> {
    private final Function<G, Optional<Link>> findGitHubRepositoryLink;
    private final Function<S, Optional<Link>> findStackOverflowQuestionLink;
    private final FindOrDoService<S, StackOverflowParserResult> stackOverflowQuestionsService;
    private final FindOrDoService<G, GitHubParserResult> gitHubRepositoriesService;
    private Supplier<Optional<Link>> onFind;

    public Optional<Link> find(LinkParserResult linkParserResult) {
        linkParserResult.acceptVisitor(new Visitor());
        return onFind.get();
    }

    class Visitor implements LinkParserResultVisitor {
        @Override
        public void visit(GitHubParserResult gitHubParserResult) {
            onFind = () -> findGitHubRepositoryLink.apply(gitHubRepositoriesService.findOrCreate(gitHubParserResult));
        }

        @Override
        public void visit(StackOverflowParserResult stackOverflowParserResult) {
            onFind = () -> findStackOverflowQuestionLink
                    .apply(stackOverflowQuestionsService.findOrCreate(stackOverflowParserResult));
        }
    }
}
