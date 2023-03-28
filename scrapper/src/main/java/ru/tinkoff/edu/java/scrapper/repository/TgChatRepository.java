package ru.tinkoff.edu.java.scrapper.repository;

import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.exception.TgChatNotFoundException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TgChatRepository {
    private final Set<Long> ids;

    public TgChatRepository() {
        this.ids = new ConcurrentHashMap<Long, Integer>().keySet(0);
    }

    public void add(long id) {
        ids.add(id);
    }

    public void delete(long id) {
        ids.remove(id);
    }

    public long find(long id) {
        if (ids.contains(id)) {
            return id;
        } else {
            throw new TgChatNotFoundException();
        }
    }
}
