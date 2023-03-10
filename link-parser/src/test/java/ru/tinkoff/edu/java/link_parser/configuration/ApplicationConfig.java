package ru.tinkoff.edu.java.link_parser.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:app.properties")
@ComponentScan("ru.tinkoff.edu.java.link_parser.github")
@ComponentScan("ru.tinkoff.edu.java.link_parser.stackoverflow")
@ComponentScan("ru.tinkoff.edu.java.link_parser")
public class ApplicationConfig {}
