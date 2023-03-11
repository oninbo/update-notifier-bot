package ru.tinkoff.edu.java.link_parser.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("ru.tinkoff.edu.java")
public class ApplicationConfig {}
