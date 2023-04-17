package ru.tinkoff.edu.java.scrapper.configuration.access_config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@RequiredArgsConstructor
public class JpaAccessConfiguration extends DataAccessConfig {
    private final AutowireCapableBeanFactory beanFactory;
    private final ApplicationContext applicationContext;

    @Override
    protected AccessType accessType() {
        return AccessType.JPA;
    }

    @Override
    protected AutowireCapableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
