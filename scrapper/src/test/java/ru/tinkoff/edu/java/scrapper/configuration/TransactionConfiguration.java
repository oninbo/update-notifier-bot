package ru.tinkoff.edu.java.scrapper.configuration;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import ru.tinkoff.edu.java.scrapper.environment.IntegrationEnvironment;

import javax.sql.DataSource;

@Configuration
public class TransactionConfiguration {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory(dataSource));
        return transactionManager;
    }

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

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private SessionFactory sessionFactory(DataSource dataSource) throws HibernateException {
        var configuration = new org.hibernate.cfg.Configuration();
        var serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySetting(Environment.DATASOURCE, dataSource);
        var serviceRegistry = serviceRegistryBuilder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
