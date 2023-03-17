package ru.tinkoff.edu.java.scrapper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.edu.java.scrapper.service.TgChatService;

@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class TgChatController {
    private final TgChatService tgChatService;

    @PostMapping("/{id}")
    public void addTgChat(@PathVariable(value = "id") Long id) {
        tgChatService.add(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTgChat(@PathVariable(value = "id") Long id) {
        tgChatService.delete(id);
    }
}
