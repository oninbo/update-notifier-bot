package ru.tinkoff.edu.java.bot.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.tinkoff.edu.java.bot.dto.AddLinkRequest;
import ru.tinkoff.edu.java.bot.dto.LinkResponse;
import ru.tinkoff.edu.java.bot.dto.ListLinksResponse;
import ru.tinkoff.edu.java.bot.dto.RemoveLinkRequest;

public interface ScrapperClient {
    @GetExchange("/links")
    ListLinksResponse getLinks(
            @RequestParam(name = "Tg-Chat-Id") Long id
    );

    @PostExchange("/links")
    LinkResponse addLink(@RequestParam(name = "Tg-Chat-Id") Long id,
                         @RequestBody AddLinkRequest addLinkRequest);


    @DeleteExchange("/links")
    LinkResponse deleteLink(@RequestParam(name = "Tg-Chat-Id") Long id,
                            @RequestBody RemoveLinkRequest removeLinkRequest);


    @PostExchange("/tg-chat/{id}")
    void addTgChat(@PathVariable(value = "id") Long id);

    @DeleteExchange("/tg-chat/{id}")
    void deleteTgChat(@PathVariable(value = "id") Long id);
}
