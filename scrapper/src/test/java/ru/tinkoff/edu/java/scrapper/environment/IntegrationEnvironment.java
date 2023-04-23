package ru.tinkoff.edu.java.scrapper.environment;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.SneakyThrows;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

public class IntegrationEnvironment {
    @SneakyThrows
    public static Connection getDatabaseConnection() {
        return DriverManager.getConnection(
                DATABASE_CONTAINER.getJdbcUrl(),
                DATABASE_CONTAINER.getUsername(),
                DATABASE_CONTAINER.getPassword()
        );
    }

    public static final JdbcDatabaseContainer<?> DATABASE_CONTAINER;

    static {
        DATABASE_CONTAINER = new PostgreSQLContainer<>("postgres:15");
        DATABASE_CONTAINER.start();
        var databaseConnection = new JdbcConnection(getDatabaseConnection());
        try (
                var liquibase = new liquibase.Liquibase(
                        "master.yml",
                        new DirectoryResourceAccessor(getMigrationsDirectory()),
                        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection)
                )
        ) {
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static Path getMigrationsDirectory() {
        return FileSystems.getDefault().getPath(".").resolve("migrations");
    }
}
