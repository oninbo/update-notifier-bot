package ru.tinkoff.edu.java.scrapper.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.edu.java.scrapper.dto.AddLinkRequest;
import ru.tinkoff.edu.java.scrapper.dto.LinkResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListLinksResponse;
import ru.tinkoff.edu.java.scrapper.dto.RemoveLinkRequest;
import ru.tinkoff.edu.java.scrapper.service.LinksService;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {
    private final LinksService linksService;

    @GetMapping
    public ListLinksResponse getLinks(@RequestParam("Tg-Chat-Id") Long chatId) {
        var links = linksService
                .getLinks(chatId)
                .stream()
                .map(link -> new LinkResponse(link.id(), link.url()))
                .toList();

        return new ListLinksResponse(links, links.size());
    }

    @PostMapping
    public LinkResponse addLink(@RequestParam("Tg-Chat-Id") Long chatId,
                                @RequestBody @Valid AddLinkRequest addLinkRequest) {
        var link = linksService.addLink(chatId, addLinkRequest.link());
        return new LinkResponse(link.id(), link.url());
    }

    @DeleteMapping
    public LinkResponse deleteLink(@RequestParam("Tg-Chat-Id") Long chatId,
                                   @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        var link = linksService.deleteLink(chatId, removeLinkRequest.link());
        return new LinkResponse(link.id(), link.url());
    }
}
