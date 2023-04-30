package ru.tinkoff.edu.java.scrapper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.client.BotClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.queue.producer.UpdatesQueueProducer;

@Service
@RequiredArgsConstructor
public class LinkUpdateSendService {
    private final BotClient botClient;
    private final UpdatesQueueProducer queueProducer;
    private final ApplicationConfig applicationConfig;

    public void sendUpdate(LinkUpdate linkUpdate) {
        if (applicationConfig.useQueue()) {
            queueProducer.sendLinkUpdate(linkUpdate);
        } else {
            botClient.sendLinkUpdates(linkUpdate);
        }
    }
}
