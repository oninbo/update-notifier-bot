package ru.tinkoff.edu.java.scrapper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;

@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class TgChatsController {
    @Qualifier("jooqTgChatsService")
    private final TgChatsService tgChatsService;

    @PostMapping("/{id}")
    public void addTgChat(@PathVariable(value = "id") Long id) {
        tgChatsService.addTgChat(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTgChat(@PathVariable(value = "id") Long id) {
        tgChatsService.deleteTgChat(id);
    }
}
