package ru.tinkoff.edu.java.scrapper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.repository.TgChatsRepository;

@Service
@RequiredArgsConstructor
public class TgChatsService {
    private final TgChatsRepository tgChatsRepository;

    public void add(long id) {
        tgChatsRepository.add(new TgChatAddParams(id));
    }

    public void delete(long id) {
        TgChat tgChat = tgChatsRepository.find(id);
        tgChatsRepository.remove(tgChat.id());
    }
}
