package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.LinkExistsException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotFoundException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotSupportedException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.LinksService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkBuilder;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkFinder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class JdbcLinksService implements LinksService {
    private final JdbcLinksRepository jdbcLinksRepository;
    private final JdbcTgChatsRepository jdbcTgChatsRepository;
    private final ApplicationConfig applicationConfig;
    private final LinkParserService linkParserService;
    private final JdbcGitHubRepositoriesService gitHubRepositoriesService;
    private final JdbcStackOverflowQuestionsService stackOverflowQuestionsService;

    @Override
    public List<Link> getLinks(Long chatId) {
        TgChat tgChat = getTgChat(chatId);
        return jdbcLinksRepository.findAll(tgChat);
    }

    @Override
    public Link addLink(Long chatId, URI url) {
        TgChat tgChat = getTgChat(chatId);

        var linkBuilder = new LinkBuilder(
                gitHubRepository -> new Add(this, tgChat, url).add(gitHubRepository),
                stackOverflowQuestion -> new Add(this, tgChat, url).add(stackOverflowQuestion),
                stackOverflowQuestionsService,
                gitHubRepositoriesService
        );
        return linkBuilder.build(getLinkParserResult(url));
    }

    @Transactional
    public Link addLink(TgChat tgChat, URI url, GitHubRepository gitHubRepository) {
        checkIfLinkExists(jdbcLinksRepository.find(tgChat, gitHubRepository));
        return jdbcLinksRepository.add(new LinkAddParams(url, tgChat, gitHubRepository));
    }

    @Transactional
    public Link addLink(TgChat tgChat, URI url, StackOverflowQuestion stackOverflowQuestion) {
        checkIfLinkExists(jdbcLinksRepository.find(tgChat, stackOverflowQuestion));
        return jdbcLinksRepository.add(new LinkAddParams(url, tgChat, stackOverflowQuestion));
    }

    @Override
    public Link deleteLink(Long chatId, URI url) {
        var tgChat = getTgChat(chatId);
        var linkResult = new LinkFinder(
                gitHubRepository -> jdbcLinksRepository.find(tgChat, gitHubRepository),
                question -> jdbcLinksRepository.find(tgChat, question),
                stackOverflowQuestionsService,
                gitHubRepositoriesService
        )
                .find(getLinkParserResult(url));


        if (linkResult.isEmpty()) {
            throw new LinkNotFoundException(applicationConfig);
        }

        var link = linkResult.get();
        jdbcLinksRepository.remove(link.id());
        return link;
    }

    private TgChat getTgChat(Long chatId) {
        var result = jdbcTgChatsRepository.find(chatId);
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

    @RequiredArgsConstructor
    static class Add {
        private final JdbcLinksService linksService;
        private final TgChat tgChat;
        private final URI url;

        public Link add(GitHubRepository gitHubRepository) {
            return linksService.addLink(tgChat, url, gitHubRepository);
        }

        public Link add(StackOverflowQuestion question) {
            return linksService.addLink(tgChat, url, question);
        }
    }
}
