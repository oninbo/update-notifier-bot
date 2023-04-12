package ru.tinkoff.edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.client.BotClient;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.configuration.SchedulerConfig;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.service.UpdatesService;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    @SuppressWarnings("unused")
    private final SchedulerConfig schedulerConfig;
    private final ApplicationConfig applicationConfig;
    private final List<UpdatesService<?>> updatesServices;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@schedulerConfig.getInterval()}")
    public void update() {
        updatesServices.forEach(this::processUpdates);
    }

    @Transactional
    public <T> void processUpdates(UpdatesService<T> updatesService) {
        var objects = updatesService.getObjectsForUpdate(applicationConfig.scheduler().batchSize());
        List<LinkUpdate> linkUpdates = updatesService.getLinkUpdates(objects);
        updatesService.updateUpdatedAt(objects, OffsetDateTime.now());
        linkUpdates.forEach(botClient::sendLinkUpdates);
    }
}
