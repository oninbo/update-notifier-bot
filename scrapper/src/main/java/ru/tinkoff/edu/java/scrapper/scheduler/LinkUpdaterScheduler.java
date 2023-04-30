package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.service.LinkUpdateSendService;
import ru.tinkoff.edu.java.scrapper.service.LinksUpdatesService;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    @SuppressWarnings("unused")
    private final SchedulerConfig schedulerConfig;
    private final ApplicationConfig applicationConfig;
    private final List<LinksUpdatesService<?>> linksUpdatesServices;
    private final LinkUpdateSendService linkUpdateSendService;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    public void update() {
        linksUpdatesServices.forEach(this::processUpdates);
    }

    @Transactional
    public <T> void processUpdates(LinksUpdatesService<T> linksUpdatesService) {
        var objects = linksUpdatesService.getForLinksUpdate(applicationConfig.scheduler().batchSize());
        List<LinkUpdate> linkUpdates = linksUpdatesService.getLinkUpdates(objects);
        linksUpdatesService.updateUpdatedAt(objects, OffsetDateTime.now());
        linkUpdates.forEach(linkUpdateSendService::sendUpdate);
    }
}
