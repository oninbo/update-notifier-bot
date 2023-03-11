package ru.tinkoff.edu.java.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
// Не получилось использовать ComponentScan для получения бинов из link-parser, Spring в bot почему-то их не видит,
// пришлось описывать бины в ApplicationConfig :(
@Import(ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig.class)
public class BotApplication {
    public static void main(String[] args) {
        var ctx = SpringApplication.run(BotApplication.class, args);

        // Проверяем, что в модуле bot доступен класс из модуля link-parser
        // В hw1 не требуется этого делать, но, возможно, понадобится в будущем, да и просто попробовать было интересно
        var linkParserService = ctx.getBean(LinkParserService.class);
        linkParserService.parse("https://abc.xyz");

        ApplicationConfig config = ctx.getBean(ApplicationConfig.class);
        System.out.println(config);
    }
}
