package ru.tinkoff.edu.java.bot.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class DeadLetterQueueConfiguration {
    @Bean(name = "dlqQueue")
    Queue queue(final ApplicationConfig applicationConfig) {
        return new Queue(applicationConfig.rabbitMQ().deadLetterQueueName());
    }

    @Bean(name = "dlqExchange")
    DirectExchange exchange(final ApplicationConfig applicationConfig) {
        return new DirectExchange(applicationConfig.rabbitMQ().deadLetterExchangeName());
    }

    @Bean(name = "dlqBinding")
    Binding binding(final @Qualifier("dlqQueue") Queue queue, final @Qualifier("dlqExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).withQueueName();
    }
}
