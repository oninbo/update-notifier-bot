package ru.tinkoff.edu.java.scrapper.service.jooq;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.*;
import ru.tinkoff.edu.java.scrapper.exception.LinkExistsException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotFoundException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotSupportedException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jooq.JooqTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.LinksService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkBuilder;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkFinder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class JooqLinksService implements LinksService {
    private final JooqLinksRepository linksRepository;
    private final JooqTgChatsRepository tgChatsRepository;
    private final ApplicationConfig applicationConfig;
    private final JooqStackOverflowQuestionsService stackOverflowQuestionsService;
    private final JooqGitHubRepositoriesService gitHubRepositoriesService;
    private final LinkParserService linkParserService;

    @Override
    public List<Link> getLinks(Long chatId) {
        return linksRepository.findAll(chatId);
    }

    @Transactional
    @Override
    public Link addLink(Long chatId, URI url) {
        TgChat tgChat = getTgChat(chatId);

        var linkBuilder = new LinkBuilder<>(
                gitHubRepository -> addLink(tgChat, url, gitHubRepository),
                stackOverflowQuestion -> addLink(tgChat, url, stackOverflowQuestion),
                stackOverflowQuestionsService,
                gitHubRepositoriesService
        );
        return linkBuilder.build(getLinkParserResult(url));
    }

    private Link addLink(TgChat tgChat, URI url, StackOverflowQuestion stackOverflowQuestion) {
        checkIfLinkExists(() -> linksRepository.find(tgChat, stackOverflowQuestion));
        return linksRepository.add(new LinkAddParams(url, tgChat, stackOverflowQuestion));
    }

    private Link addLink(TgChat tgChat, URI url, GitHubRepository gitHubRepository) {
        checkIfLinkExists(() -> linksRepository.find(tgChat, gitHubRepository));
        return linksRepository.add(new LinkAddParams(url, tgChat, gitHubRepository));
    }

    @Override
    public Link deleteLink(Long chatId, URI url) {
        var tgChat = getTgChat(chatId);
        var linkFinder = new LinkFinder<>(
                gitHubRepository -> linksRepository.find(tgChat, gitHubRepository),
                question -> linksRepository.find(tgChat, question),
                stackOverflowQuestionsService,
                gitHubRepositoriesService
        );

        return linkFinder
                .find(getLinkParserResult(url))
                .map(link -> {
                    linksRepository.remove(link.id());
                    return link;
                })
                .orElseThrow(() -> new LinkNotFoundException(applicationConfig));
    }

    private TgChat getTgChat(Long chatId) {
        var result = tgChatsRepository.find(chatId);
        if (result.isEmpty()) {
            throw new TgChatNotFoundException(applicationConfig);
        }
        return result.get();
    }

    private LinkParserResult getLinkParserResult(URI link) {
        var linkParserResult = linkParserService.parse(link);

        if (linkParserResult.isEmpty()) {
            throw new LinkNotSupportedException(applicationConfig);
        }

        return linkParserResult.get();
    }

    private void checkIfLinkExists(Supplier<Optional<Link>> link) {
        if (link.get().isPresent()) {
            throw new LinkExistsException(applicationConfig);
        }
    }
}
