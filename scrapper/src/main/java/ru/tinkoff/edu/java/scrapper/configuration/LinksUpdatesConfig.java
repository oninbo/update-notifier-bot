package ru.tinkoff.edu.java.scrapper.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.edu.java.scrapper.service.LinksUpdatesService;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LinksUpdatesConfig {
    @Bean
    public List<LinksUpdatesService<?>> jooqLinksUpdatesServices(ApplicationContext applicationContext) {
        List<LinksUpdatesService<?>> services = new ArrayList<>();
        applicationContext.getBeansOfType(LinksUpdatesService.class).forEach(
                (name, service) -> {
                    if (name.contains("jooq")) {
                        services.add(service);
                    }
                }
        );
        return services;
    }
}
