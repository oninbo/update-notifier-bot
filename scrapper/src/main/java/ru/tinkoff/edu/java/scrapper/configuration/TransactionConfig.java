package ru.tinkoff.edu.java.scrapper.configuration;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQLDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TransactionConfig {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory(dataSource));
        return transactionManager;
    }

    @Bean(name="entityManagerFactory")
    public SessionFactory sessionFactory(DataSource dataSource) throws HibernateException {
        var configuration = new org.hibernate.cfg.Configuration();
        var serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySetting(Environment.DATASOURCE, dataSource);
        serviceRegistryBuilder.applySetting(Environment.DIALECT, PostgreSQLDialect.class);
        var serviceRegistry = serviceRegistryBuilder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
