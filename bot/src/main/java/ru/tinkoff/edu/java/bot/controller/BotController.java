package ru.tinkoff.edu.java.bot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.edu.java.bot.dto.LinkUpdateRequest;
import ru.tinkoff.edu.java.link_parser.LinkParserResult;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class BotController {
    private final LinkParserService linkParserService;

    @PostMapping("/updates")
    public void updates(@Valid @RequestBody LinkUpdateRequest linkUpdateRequest) {
        Optional<LinkParserResult> result = linkParserService.parse(linkUpdateRequest.url());
        result.ifPresent(System.out::println);
    }
}
