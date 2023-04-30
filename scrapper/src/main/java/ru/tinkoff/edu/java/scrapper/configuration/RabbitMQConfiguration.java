package ru.tinkoff.edu.java.scrapper.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;

import java.util.HashMap;
import java.util.Map;

@Configuration
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
    public ClassMapper classMapper() {
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("ru.tinkoff.edu.java.bot.dto.LinkUpdate", LinkUpdate.class);

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(mappings);
        return classMapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ClassMapper classMapper) {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(classMapper);
        return jsonConverter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
