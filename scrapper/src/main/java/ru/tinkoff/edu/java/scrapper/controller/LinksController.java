package ru.tinkoff.edu.java.scrapper.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.edu.java.scrapper.configuration.Placeholder;
import ru.tinkoff.edu.java.scrapper.dto.AddLinkRequest;
import ru.tinkoff.edu.java.scrapper.dto.LinkResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListLinksResponse;
import ru.tinkoff.edu.java.scrapper.dto.RemoveLinkRequest;

@RestController
@RequestMapping("/links")
@EnableConfigurationProperties(Placeholder.class)
@RequiredArgsConstructor
public class LinksController {
    private final Placeholder placeholder;

    @GetMapping
    public ListLinksResponse getLinks(@RequestParam("Tg-Chat-Id") Long ignoredId) {
        return placeholder.listLinksResponse();
    }

    @PostMapping
    public LinkResponse addLink(@RequestParam("Tg-Chat-Id") Long id,
                                @RequestBody @Valid AddLinkRequest addLinkRequest) {
        return new LinkResponse(id, addLinkRequest.link());
    }

    @DeleteMapping
    public LinkResponse deleteLink(@RequestParam("Tg-Chat-Id") Long id,
                                   @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        return new LinkResponse(id, removeLinkRequest.link());
    }
}
