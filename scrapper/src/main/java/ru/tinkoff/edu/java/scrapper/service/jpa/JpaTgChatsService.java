package ru.tinkoff.edu.java.scrapper.service.jpa;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.edu.java.scrapper.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.scrapper.dto.TgChatAddParams;
import ru.tinkoff.edu.java.scrapper.exception.TgChatExistsException;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.repository.jpa.JpaTgChatsRepository;
import ru.tinkoff.edu.java.scrapper.service.TgChatsService;

@RequiredArgsConstructor
public class JpaTgChatsService implements TgChatsService {
    private final JpaTgChatsRepository tgChatsRepository;
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
                tgChat -> tgChatsRepository.deleteById(tgChat.id()),
                () -> { throw new TgChatNotFoundException(applicationConfig); }
        );
    }
}
