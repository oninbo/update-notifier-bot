package ru.tinkoff.edu.java.scrapper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.tinkoff.edu.java.scrapper.environment.IntegrationEnvironment;

import javax.sql.DataSource;

@Configuration
public class TestDataSourceConfig {
    @Bean
    public DataSource dataSource() {
        var container = IntegrationEnvironment.DATABASE_CONTAINER;
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(container.getDriverClassName());
        dataSource.setUrl(container.getJdbcUrl());
        dataSource.setUsername(container.getUsername());
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }
}
