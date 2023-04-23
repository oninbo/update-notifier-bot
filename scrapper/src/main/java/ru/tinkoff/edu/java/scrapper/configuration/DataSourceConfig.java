package ru.tinkoff.edu.java.scrapper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource databaseDataSource(ApplicationConfig applicationConfig) {
        var config = applicationConfig.database();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(config.driver());
        dataSource.setUrl(config.url());
        dataSource.setUsername(config.username());
        dataSource.setPassword(config.password());

        return dataSource;
    }
}
