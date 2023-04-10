package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserResultVisitor;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.link_parser.github.GitHubParserResult;
import ru.tinkoff.edu.java.link_parser.stackoverflow.StackOverflowParserResult;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.LinkExistsException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotFoundException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotSupportedException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.LinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.TgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.FindOrDoService;
import ru.tinkoff.edu.java.scrapper.service.LinksService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
class JdbcLinksService implements LinksService {
    private final LinksRepository linksRepository;
    private final TgChatsRepository tgChatsRepository;
    private final ApplicationConfig applicationConfig;
    private final LinkParserService linkParserService;
    private final FindOrDoService<GitHubRepository, GitHubParserResult> gitHubRepositoriesService;
    private final FindOrDoService<StackOverflowQuestion, StackOverflowParserResult> stackOverflowQuestionsService;

    @Override
    public List<Link> getLinks(Long chatId) {
        TgChat tgChat = getTgChat(chatId);
        return linksRepository.findAll(tgChat);
    }

    @Override
    public Link addLink(Long chatId, URI url) {
        TgChat tgChat = getTgChat(chatId);

        var linkBuilder = new LinkBuilder(
                url,
                tgChat,
                getLinkParserResult(url)
        );
        return linkBuilder.build();
    }

    public Link addLink(TgChat tgChat, URI url, GitHubRepository gitHubRepository) {
        checkIfLinkExists(linksRepository.find(tgChat, gitHubRepository));
        return linksRepository.add(new LinkAddParams(url, tgChat, gitHubRepository));
    }

    public Link addLink(TgChat tgChat, URI url, StackOverflowQuestion stackOverflowQuestion) {
        checkIfLinkExists(linksRepository.find(tgChat, stackOverflowQuestion));
        return linksRepository.add(new LinkAddParams(url, tgChat, stackOverflowQuestion));
    }

    @Override
    public Link deleteLink(Long chatId, URI url) {
        var tgChat = getTgChat(chatId);
        var linkResult = new LinkFinder(tgChat, getLinkParserResult(url)).find();

        if (linkResult.isEmpty()) {
            throw new LinkNotFoundException(applicationConfig);
        }

        var link = linkResult.get();
        linksRepository.remove(link.id());
        return link;
    }

    private TgChat getTgChat(Long chatId) {
        var result = tgChatsRepository.find(chatId);
        if (result.isEmpty()) {
            throw new TgChatNotFoundException(applicationConfig);
        }
        return result.get();
    }

    private void checkIfLinkExists(Optional<Link> link) {
        if (link.isPresent()) {
            throw new LinkExistsException(applicationConfig);
        }
    }

    private LinkParserResult getLinkParserResult(URI link) {
        var linkParserResult = linkParserService.parse(link);

        if (linkParserResult.isEmpty()) {
            throw new LinkNotSupportedException(applicationConfig);
        }

        return linkParserResult.get();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class LinkBuilder implements LinkParserResultVisitor {
        private final URI url;
        private final TgChat tgChat;
        private Supplier<Link> onBuild;

        public LinkBuilder(URI url, TgChat tgChat, LinkParserResult linkParserResult) {
            this(url, tgChat);
            linkParserResult.acceptVisitor(this);
        }

        public Link build() {
            return onBuild.get();
        }

        @Override
        public void visit(GitHubParserResult gitHubParserResult) {
            onBuild = () -> addLink(tgChat, url, gitHubRepositoriesService.findOrCreate(gitHubParserResult));
        }

        @Override
        public void visit(StackOverflowParserResult stackOverflowParserResult) {
            onBuild = () -> addLink(
                    tgChat,
                    url,
                    stackOverflowQuestionsService.findOrCreate(stackOverflowParserResult)
            );
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class LinkFinder implements LinkParserResultVisitor {
        private final TgChat tgChat;
        private Supplier<Optional<Link>> onFind;

        public LinkFinder(TgChat tgChat, LinkParserResult linkParserResult) {
            this(tgChat);
            linkParserResult.acceptVisitor(this);
        }

        public Optional<Link> find() {
            return onFind.get();
        }

        @Override
        public void visit(GitHubParserResult gitHubParserResult) {
            onFind = () -> linksRepository.find(tgChat, gitHubRepositoriesService.findOrThrow(gitHubParserResult));
        }

        @Override
        public void visit(StackOverflowParserResult stackOverflowParserResult) {
            onFind = () -> linksRepository.find(
                    tgChat,
                    stackOverflowQuestionsService.findOrThrow(stackOverflowParserResult)
            );
        }
    }
}
