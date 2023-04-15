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

import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class LinkBuilder {
    private final Function<GitHubRepository, Link> addGitHubRepositoryLink;
    private final Function<StackOverflowQuestion, Link> addStackOverflowQuestionLink;
    private final FindOrDoService<StackOverflowQuestion, StackOverflowParserResult> stackOverflowQuestionsService;
    private final FindOrDoService<GitHubRepository, GitHubParserResult> gitHubRepositoriesService;

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
