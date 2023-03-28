package ru.tinkoff.edu.java.scrapper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.tinkoff.edu.java.scrapper.client.GitHubClient;
import ru.tinkoff.edu.java.scrapper.client.StackExchangeClient;

@Configuration
public class ClientConfiguration {
    @Bean
    public GitHubClient getGitHubClient(ApplicationConfig applicationConfig) {
        return createWebClient(GitHubClient.class, applicationConfig.webClient().github().baseUrl());
    }

    @Bean
    public StackExchangeClient getStackExchangeClient(ApplicationConfig applicationConfig) {
        return createWebClient(StackExchangeClient.class, applicationConfig.webClient().stackExchange().baseUrl());
    }

    private <T> T createWebClient(Class<T> clientClass, String baseUrl) {
        WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();

        return factory.createClient(clientClass);
    }
}
