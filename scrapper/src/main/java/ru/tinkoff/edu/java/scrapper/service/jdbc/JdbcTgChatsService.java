package ru.tinkoff.edu.java.scrapper.service.jdbc;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.exception.TgChatExistsException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jdbc.JdbcTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;

@RequiredArgsConstructor
public class JdbcTgChatsService implements TgChatsService {
    private final JdbcTgChatsRepository jdbcTgChatsRepository;
    private final ApplicationConfig applicationConfig;

    @Override
    public void addTgChat(long chatId) {
        if (jdbcTgChatsRepository.find(chatId).isPresent()) {
            throw new TgChatExistsException(applicationConfig);
        }

        jdbcTgChatsRepository.add(new TgChatAddParams(chatId));
    }

    @Override
    public void deleteTgChat(long chatId) {
        jdbcTgChatsRepository.find(chatId).ifPresentOrElse(
                tgChat -> jdbcTgChatsRepository.remove(tgChat.id()),
                () -> { throw new TgChatNotFoundException(applicationConfig); }
        );
    }
}
