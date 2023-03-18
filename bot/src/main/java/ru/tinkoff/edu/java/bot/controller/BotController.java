package ru.tinkoff.edu.java.bot.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.edu.java.bot.dto.LinkUpdate;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

import java.util.Optional;

@RestController
public class BotController {
    private final LinkParserService linkParserService;

    public BotController(LinkParserService linkParserService) {
        this.linkParserService = linkParserService;
    }

    @PostMapping("/updates")
    public void updates(@Valid @RequestBody LinkUpdate linkUpdate) {
        Optional<LinkParserResult> result = linkParserService.parse(linkUpdate.url());
        result.ifPresent(System.out::println);
    }
}
