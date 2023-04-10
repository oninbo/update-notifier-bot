package ru.tinkoff.edu.java.scrapper.service;

import org.apache.commons.lang3.ObjectUtils;
import ru.tinkoff.edu.java.scrapper.dto.LinkUpdate;
import ru.tinkoff.edu.java.scrapper.dto.LinkWithChatId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LinkUpdateUtils {
    public static <T> List<LinkUpdate> getUpdates(
            Iterable<T> updated,
            Function<T, OffsetDateTime> getFetchedUpdatedAt,
            Function<T, List<LinkWithChatId>> getLinks,
            Function<T, OffsetDateTime> getUpdatedAt,
            BiFunction<T, OffsetDateTime, Void> updateUpdatedAt
    ) {
        List<LinkUpdate> linkUpdates = new ArrayList<>();
        for (var u : updated) {
            var fetchedUpdatedAt = getFetchedUpdatedAt.apply(u);

            if (Objects.isNull(fetchedUpdatedAt)) {
                continue;
            }

            var updatedAt = getUpdatedAt.apply(u);

            // Если в бд updated_at null, ничего не делаем
            // Если в бд updated_at равен дате из api, ничего не делаем
            // Если в бд updated_at меньше даты из api, записываем ссылку, чтобы отправить уведомление об обновлении
            if (ObjectUtils.compare(updatedAt, fetchedUpdatedAt, true) < 0) {
                var links = getLinks.apply(u);

                if (links.isEmpty()) {
                    continue;
                }

                var link = links.get(0);
                linkUpdates.add(
                        new LinkUpdate(
                                link.id(),
                                link.url(),
                                null,
                                links.stream().map(LinkWithChatId::chatId).toList()
                        )
                );
            }

            if (!Objects.equals(updatedAt, fetchedUpdatedAt)) {
                updateUpdatedAt.apply(u, fetchedUpdatedAt);
            }
        }
        return linkUpdates;
    }
}
