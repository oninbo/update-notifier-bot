package ru.tinkoff.edu.java.scrapper.service;

import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.repository.TgChatRepository;

@Service
public class TgChatService {
    private final TgChatRepository tgChatRepository;

    public TgChatService(TgChatRepository tgChatRepository) {
        this.tgChatRepository = tgChatRepository;
    }

    public void add(long id) {
        tgChatRepository.add(new TgChatAddParams(id));
    }

    public void delete(long id) {
        TgChat tgChat = tgChatRepository.find(id);
        tgChatRepository.remove(tgChat.id());
    }
}
