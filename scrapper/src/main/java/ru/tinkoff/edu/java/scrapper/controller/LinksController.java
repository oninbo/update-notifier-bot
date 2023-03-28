package ru.tinkoff.edu.java.scrapper.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.edu.java.scrapper.dto.AddLinkRequest;
import ru.tinkoff.edu.java.scrapper.dto.LinkResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListLinksResponse;
import ru.tinkoff.edu.java.scrapper.dto.RemoveLinkRequest;

import java.util.List;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {
    @GetMapping
    public ListLinksResponse getLinks(@RequestParam("Tg-Chat-Id") Long ignoredId) {
        return new ListLinksResponse(
                List.of(new LinkResponse(1L, "https://github.com/sanyarnd/tinkoff-java-course-2022")),
                1
        );
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
