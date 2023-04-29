package ru.tinkoff.edu.java.bot.queue.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import ru.tinkoff.edu.java.bot.dto.LinkUpdate;
import ru.tinkoff.edu.java.bot.service.LinkUpdatesService;

@RabbitListener(queues = "${app.rabbit_mq.queue_name}")
@RequiredArgsConstructor
public class UpdatesQueueListener {
    private final LinkUpdatesService linkUpdatesService;

    @RabbitHandler
    public void receive(LinkUpdate linkUpdate) {
        linkUpdatesService.sendUpdate(linkUpdate);
    }
}
