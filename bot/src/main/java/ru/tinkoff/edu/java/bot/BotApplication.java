package ru.tinkoff.edu.java.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.TgBotService;
import ru.tinkoff.edu.java.link_parser.configuration.LinkParserConfig;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
@Import(LinkParserConfig.class)
public class BotApplication {
    public static void main(String[] args) {
        var ctx = SpringApplication.run(BotApplication.class, args);
        ctx.getBean(TgBotService.class).listen();
    }
}
