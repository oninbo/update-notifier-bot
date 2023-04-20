package ru.tinkoff.edu.java.scrapper.service.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.entity.GitHubRepositoryEntity;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;
import ru.tinkoff.edu.java.scrapper.entity.StackOverflowQuestionEntity;
import ru.tinkoff.edu.java.scrapper.entity.TgChatEntity;
import ru.tinkoff.edu.java.scrapper.exception.LinkExistsException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotFoundException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotSupportedException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.mapper.LinkMapper;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaLinksRepository;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.LinksService;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkBuilder;
import ru.tinkoff.edu.java.scrapper.service.utils.LinkFinder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class JpaLinksService implements LinksService {
    private final JpaLinksRepository linksRepository;
    private final JpaTgChatsRepository tgChatsRepository;
    private final ApplicationConfig applicationConfig;
    private final JpaStackOverflowQuestionsService stackOverflowQuestionsService;
    private final JpaGitHubRepositoriesService gitHubRepositoriesService;
    private final LinkParserService linkParserService;
    private final LinkMapper linkMapper;

    @Override
    public List<Link> getLinks(Long chatId) {
        return linksRepository.findAllByChatId(chatId)
                .stream()
                .map(linkMapper::fromEntity)
                .toList();
    }

    @Transactional
    @Override
    public Link addLink(Long chatId, URI url) {
        TgChatEntity tgChat = getTgChat(chatId);

        var linkBuilder = new LinkBuilder<>(
                gitHubRepository -> addLink(tgChat, url, gitHubRepository),
                stackOverflowQuestion -> addLink(tgChat, url, stackOverflowQuestion),
                stackOverflowQuestionsService,
                gitHubRepositoriesService
        );
        return linkBuilder.build(getLinkParserResult(url));
    }

    private Link addLink(TgChatEntity tgChat, URI url, StackOverflowQuestionEntity stackOverflowQuestion) {
        checkIfLinkExists(() -> linksRepository.findByTgChatAndStackOverflowQuestion(tgChat, stackOverflowQuestion));
        return linksRepository.add(url, tgChat, stackOverflowQuestion);
    }

    private Link addLink(TgChatEntity tgChat, URI url, GitHubRepositoryEntity gitHubRepository) {
        checkIfLinkExists(() -> linksRepository.findByTgChatAndGitHubRepository(tgChat, gitHubRepository));
        return linksRepository.add(url, tgChat, gitHubRepository);
    }

    @Override
    public Link deleteLink(Long chatId, URI url) {
        var tgChat = getTgChat(chatId);
        var linkFinder = new LinkFinder<>(
                gitHubRepository -> linksRepository
                        .findByTgChatAndGitHubRepository(tgChat, gitHubRepository)
                        .map(linkMapper::fromEntity),
                question -> linksRepository
                        .findByTgChatAndStackOverflowQuestion(tgChat, question)
                        .map(linkMapper::fromEntity),
                stackOverflowQuestionsService,
                gitHubRepositoriesService
        );

        return linkFinder
                .find(getLinkParserResult(url))
                .map(link -> {
                    linksRepository.deleteById(link.id());
                    return link;
                })
                .orElseThrow(() -> new LinkNotFoundException(applicationConfig));
    }

    private TgChatEntity getTgChat(Long chatId) {
        var result = tgChatsRepository.findByChatId(chatId);
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

    private void checkIfLinkExists(Supplier<Optional<LinkEntity>> link) {
        if (link.get().isPresent()) {
            throw new LinkExistsException(applicationConfig);
        }
    }
}
