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
import ru.tinkoff.edu.java.bot.service.LinkUpdatesService;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@RestController
@RequiredArgsConstructor
public class BotController {
    private final LinkUpdatesService linkUpdatesService;
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;

    @PostMapping("/updates")
    public void linkUpdates(@Valid @RequestBody LinkUpdate linkUpdate) {
        linkUpdatesService.sendUpdate(linkUpdate);
    }

    @PostMapping("/stackoverflowAnswerUpdates")
    public void stackOverflowAnswerUpdates(@Valid @RequestBody StackOverflowAnswerUpdate update) {
        var messageText = String.format(
                applicationConfig.message().stackoverflowAnswerUpdate(),
                update.answerUrl(),
                update.questionUrl()
        );
        update.chatIds().forEach(id -> userResponseService.sendMessage(id, messageText));
    }

    @PostMapping("/githubIssueUpdates")
    public void githubIssueUpdates(@Valid @RequestBody GitHubIssueUpdate update) {
        var messageText = String.format(
                applicationConfig.message().githubIssueUpdate(),
                update.issueUrl(),
                update.repository().name(),
                update.repository().url(),
                update.repository().username()
        );
        update.chatIds().forEach(id -> userResponseService.sendMessage(id, messageText));
    }
}
