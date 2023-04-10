package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;

public interface BotClient {
    @PostExchange("/updates")
    void sendUpdates(@RequestBody LinkUpdate linkUpdate);
}
