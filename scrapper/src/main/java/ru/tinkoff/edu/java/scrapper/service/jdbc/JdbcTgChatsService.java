package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.dto.TgChat;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.repository.TgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;

@Service
@RequiredArgsConstructor
class JdbcTgChatsService implements TgChatsService {
    private final TgChatsRepository tgChatsRepository;

    @Override
    public void add(long id) {
        tgChatsRepository.add(new TgChatAddParams(id));
    }

    @Override
    public void delete(long id) {
        TgChat tgChat = tgChatsRepository.find(id);
        tgChatsRepository.remove(tgChat.id());
    }
}
