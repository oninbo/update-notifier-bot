package ru.tinkoff.edu.java.scrapper.configuration.access_config;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.tinkoff.edu.java.scrapper.service.*;

import java.util.ArrayList;
import java.util.List;

public abstract class DataAccessConfig {
    @Bean
    public LinksService linksService(
            StackOverflowAnswersService ignoredStackOverflowAnswersService,
            GitHubIssuesService ignoredGitHubIssuesService
    ) {
        return createServiceBean(LinksService.class);
    }

    @Bean
    public GitHubIssuesService gitHubIssuesService() {
        return createServiceBean("GitHubRepositoriesService");
    }

    @Bean
    public StackOverflowAnswersService stackOverflowAnswersService() {
        return createServiceBean("StackOverflowQuestionsService");
    }

    @Bean
    public TgChatsService tgChatsService() {
        return createServiceBean(TgChatsService.class);
    }

    @Bean
    public List<LinksUpdatesService<?>> linksUpdatesServices() {
        List<LinksUpdatesService<?>> services = new ArrayList<>();
        getServiceBeans(LinksUpdatesService.class).forEach(services::add);
        return services;
    }

    protected abstract AccessType accessType();

    protected abstract AutowireCapableBeanFactory getBeanFactory();

    protected abstract ApplicationContext getApplicationContext();

    private <T> T createServiceBean(Class<T> ofClass) {
        return createServiceBean(ofClass.getSimpleName());
    }

    private <T> T createServiceBean(String simpleClassName) {
        String accessType = accessType().toString().toLowerCase();
        String className = accessType.substring(0, 1).toUpperCase() + accessType.substring(1) + simpleClassName;
        // Класс виден только внутри своего пакета, чтобы вне пакета нельзя было добавить зависимость от реализации,
        // поэтому ищем класс по названию
        String fullClassName = String.format("ru.tinkoff.edu.java.scrapper.service.%s.%s", accessType, className);
        try {
            //noinspection unchecked
            return (T) getBeanFactory().createBean(Class.forName(fullClassName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> getServiceBeans(@SuppressWarnings("SameParameterValue") Class<T> ofClass) {
        List<T> services = new ArrayList<>();
        String accessType = accessType().toString().toLowerCase();
        getApplicationContext().getBeansOfType(ofClass).forEach(
                (name, service) -> {
                    if (name.contains(accessType)) {
                        services.add(service);
                    }
                }
        );
        return services;
    }

    public enum AccessType {
        JDBC,
        JPA,
        JOOQ
    }
}
