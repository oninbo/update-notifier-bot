package ru.tinkoff.edu.java.bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    public ScrapperClient getScrapperClient(ApplicationConfig applicationConfig) {
        return createWebClient(ScrapperClient.class, applicationConfig.webClient().scrapper().baseUrl());
    }

    @SuppressWarnings("SameParameterValue")
    private <T> T createWebClient(Class<T> clientClass, String baseUrl) {
        WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();

        return factory.createClient(clientClass);
    }
}
