package ru.tinkoff.edu.java.scrapper.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.edu.java.scrapper.configuration.Placeholder;
import ru.tinkoff.edu.java.scrapper.dto.AddLinkRequest;
import ru.tinkoff.edu.java.scrapper.dto.LinkResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListLinksResponse;
import ru.tinkoff.edu.java.scrapper.dto.RemoveLinkRequest;
import ru.tinkoff.edu.java.scrapper.service.TgChatService;

@RestController
@EnableConfigurationProperties(Placeholder.class)
public class ScrapperController {
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private Placeholder placeholder;
    private final TgChatService tgChatService;

    public ScrapperController(TgChatService tgChatService) {
        this.tgChatService = tgChatService;
    }

    @PostMapping("/tg-chat/{id}")
    public void addTgChat(@PathVariable(value = "id") Long id) {
        tgChatService.add(id);
    }

    @DeleteMapping("/tg-chat/{id}")
    public void deleteTgChat(@PathVariable(value = "id") Long id) {
        tgChatService.delete(id);
    }

    @GetMapping("/links")
    public ListLinksResponse getLinks() {
        return placeholder.listLinksResponse();
    }

    @PostMapping("/links")
    public LinkResponse addLink(@RequestParam("Tg-Chat-Id") Long id,
                                @RequestBody @Valid AddLinkRequest addLinkRequest) {
        return new LinkResponse(id, addLinkRequest.link());
    }

    @DeleteMapping("/links")
    public LinkResponse deleteLink(@RequestParam("Tg-Chat-Id") Long id,
                                   @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        return new LinkResponse(id, removeLinkRequest.link());
    }
}
