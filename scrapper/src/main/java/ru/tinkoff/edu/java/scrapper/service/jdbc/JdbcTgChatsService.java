package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.exception.TgChatExistsException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.TgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;

@Service
@RequiredArgsConstructor
class JdbcTgChatsService implements TgChatsService {
    private final TgChatsRepository tgChatsRepository;
    private final ApplicationConfig applicationConfig;

    @Override
    public void addTgChat(long chatId) {
        if (tgChatsRepository.find(chatId).isPresent()) {
            throw new TgChatExistsException(applicationConfig);
        }

        tgChatsRepository.add(new TgChatAddParams(chatId));
    }

    @Override
    public void deleteTgChat(long chatId) {
        tgChatsRepository.find(chatId).ifPresentOrElse(
                tgChat -> tgChatsRepository.remove(tgChat.id()),
                () -> { throw new TgChatNotFoundException(applicationConfig); }
        );
    }
}