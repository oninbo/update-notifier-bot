package ru.tinkoff.edu.java.bot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.GitHubIssueUpdate;
import ru.tinkoff.edu.java.bot.dto.LinkUpdate;
import ru.tinkoff.edu.java.bot.dto.StackOverflowAnswerUpdate;
import ru.tinkoff.edu.java.bot.exception.LinkNotSupportedException;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.utils.LinkParseResultPresenter;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

@RestController
@RequiredArgsConstructor
public class BotController {
    private final LinkParserService linkParserService;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;

    @PostMapping("/updates")
    public void linkUpdates(@Valid @RequestBody LinkUpdate linkUpdate) {
        LinkParserResult result = linkParserService.parse(linkUpdate.url())
                .orElseThrow(LinkNotSupportedException::new);

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(applicationConfig.message().update());
        messageBuilder.append(" ");

        var linkPresenter = new LinkParseResultPresenter(messageBuilder, linkUpdate.url().toString());
        linkPresenter.present(result);

        for (Long chatId : linkUpdate.tgChatIds()) {
            userResponseService.sendMessage(chatId, messageBuilder.toString());
        }
    }

    @PostMapping("/stackoverflowAnswerUpdates")
    public void stackOverflowAnswerUpdates(@Valid @RequestBody StackOverflowAnswerUpdate update) {
        var messageText = String.format(
                "Получен новый [ответ](%s) на [вопрос](%s) на Stack Overflow", // TODO: move to config
                update.answerUrl(),
                update.questionUrl()
        );
        update.chatIds().forEach(id -> userResponseService.sendMessage(id, messageText));
    }

    @PostMapping("/githubIssueUpdates")
    public void githubIssueUpdates(@Valid @RequestBody GitHubIssueUpdate update) {
        var messageText = String.format(
                "Добавлен новый [тикет](%s) в GitHub [репозиторий](%s)", // TODO: move to config
                update.issueUrl(),
                update.repositoryUrl()
        );
        update.chatIds().forEach(id -> userResponseService.sendMessage(id, messageText));
    }
}
