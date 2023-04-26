package ru.tinkoff.edu.java.scrapper.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import ru.tinkoff.edu.java.scrapper.dto.GitHubIssueUpdate;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;

public interface BotClient {
    @PostExchange("/updates")
    void sendLinkUpdates(@RequestBody LinkUpdate linkUpdate);
    @PostExchange("/stackoverflowAnswerUpdates")
    void sendStackOverflowAnswerUpdates(@RequestBody StackOverflowAnswerUpdate linkUpdate);
    @PostExchange("/githubIssueUpdates")
    void sendGithubIssueUpdates(@Valid @RequestBody GitHubIssueUpdate update);
}
