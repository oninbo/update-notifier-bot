package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.client.BotClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.service.LinksUpdatesService;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    @SuppressWarnings("unused")
    private final SchedulerConfig schedulerConfig;
    private final ApplicationConfig applicationConfig;

    @Qualifier("jooqLinksUpdatesServices")
    private final List<LinksUpdatesService<?>> linksUpdatesServices;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    public void update() {
        linksUpdatesServices.forEach(this::processUpdates);
    }

    @Transactional
    public <T> void processUpdates(LinksUpdatesService<T> linksUpdatesService) {
        var objects = linksUpdatesService.getForLinksUpdate(applicationConfig.scheduler().batchSize());
        List<LinkUpdate> linkUpdates = linksUpdatesService.getLinkUpdates(objects);
        linksUpdatesService.updateUpdatedAt(objects, OffsetDateTime.now());
        linkUpdates.forEach(botClient::sendLinkUpdates);
    }
}
