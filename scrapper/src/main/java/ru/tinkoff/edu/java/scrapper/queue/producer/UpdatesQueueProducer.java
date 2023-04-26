package ru.tinkoff.edu.java.scrapper.queue.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;

@Service
@RequiredArgsConstructor
public class UpdatesQueueProducer {
    private final RabbitTemplate rabbitTemplate;
    private final Binding binding;
    private final MessageConverter messageConverter;

    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        Message message = messageConverter.toMessage(linkUpdate, new MessageProperties());
        rabbitTemplate.convertAndSend(binding.getRoutingKey(), message);
    }
}
