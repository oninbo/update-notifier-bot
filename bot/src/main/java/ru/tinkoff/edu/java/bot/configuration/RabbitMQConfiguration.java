package ru.tinkoff.edu.java.bot.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfiguration {
    @Bean
    Queue queue(ApplicationConfig applicationConfig) {
        return QueueBuilder
                .durable(applicationConfig.rabbitMQ().queueName())
                .deadLetterExchange(applicationConfig.rabbitMQ().deadLetterExchangeName())
                .deadLetterRoutingKey(applicationConfig.rabbitMQ().deadLetterQueueName())
                .build();
    }

    @Bean
    DirectExchange exchange(ApplicationConfig applicationConfig) {
        return new DirectExchange(applicationConfig.rabbitMQ().exchangeName());
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).withQueueName();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
