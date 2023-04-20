package ru.tinkoff.edu.java.scrapper.service.utils;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;

import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class LinkBuilder<S, G> {
    private final Function<G, Link> addGitHubRepositoryLink;
    private final Function<S, Link> addStackOverflowQuestionLink;
    private final FindOrDoService<S, StackOverflowParserResult> stackOverflowQuestionsService;
    private final FindOrDoService<G, GitHubParserResult> gitHubRepositoriesService;

    private Supplier<Link> onBuild;

    public Link build(LinkParserResult linkParserResult) {
        linkParserResult.acceptVisitor(new Visitor());
        return onBuild.get();
    }

    class Visitor implements LinkParserResultVisitor {
        @Override
        public void visit(GitHubParserResult gitHubParserResult) {
            onBuild = () -> addGitHubRepositoryLink.apply(gitHubRepositoriesService.findOrCreate(gitHubParserResult));
        }

        @Override
        public void visit(StackOverflowParserResult stackOverflowParserResult) {
            onBuild = () -> addStackOverflowQuestionLink
                    .apply(stackOverflowQuestionsService.findOrCreate(stackOverflowParserResult));
        }
    }
}
