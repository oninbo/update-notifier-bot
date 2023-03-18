package ru.tinkoff.edu.java.bot;

import jakarta.validation.Valid;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.dto.LinkUpdate;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
@Import(ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig.class)
@RestController
public class BotApplication {
    private final LinkParserService linkParserService;

    public BotApplication(LinkParserService linkParserService) {
        this.linkParserService = linkParserService;
    }

    @PostMapping("/updates")
    public void sendUpdate(@Valid @RequestBody LinkUpdate linkUpdate) {
        linkParserService.parse(linkUpdate.url());
        System.out.println(linkUpdate);
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
