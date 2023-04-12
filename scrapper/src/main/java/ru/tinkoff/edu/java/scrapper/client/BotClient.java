package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.StackOverflowAnswerUpdate;

public interface BotClient {
    @PostExchange("/updates")
    void sendLinkUpdates(@RequestBody LinkUpdate linkUpdate);

    @PostExchange("/stackoverflow_answer_updates")
    void sendStackOverflowAnswerUpdates(@RequestBody StackOverflowAnswerUpdate linkUpdate);
}
