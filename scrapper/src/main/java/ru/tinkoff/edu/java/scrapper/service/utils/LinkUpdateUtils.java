package ru.tinkoff.edu.java.scrapper.service.utils;

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
            Iterable<T> trackedObjects,
            Function<T, OffsetDateTime> getFetchedUpdatedAt,
            BiFunction<T, OffsetDateTime, List<LinkWithChatId>> getLinks,
            Function<T, OffsetDateTime> getUpdatedAt,
            Function<T, OffsetDateTime> getCreatedAt
    ) {
        List<LinkUpdate> linkUpdates = new ArrayList<>();
        for (var object : trackedObjects) {
            var fetchedUpdatedAt = getFetchedUpdatedAt.apply(object);

            if (Objects.isNull(fetchedUpdatedAt)) {
                continue;
            }

            var updatedAt = ObjectUtils.max(getUpdatedAt.apply(object), getCreatedAt.apply(object));
            if (updatedAt.isBefore(fetchedUpdatedAt)) {
                var links = getLinks.apply(object, fetchedUpdatedAt);

                if (links.isEmpty()) {
                    continue;
                }

                var link = links.get(0);
                linkUpdates.add(
                        new LinkUpdate(
                                link.id(),
                                link.url(),
                                links.stream().map(LinkWithChatId::chatId).toList()
                        )
                );
            }
        }
        return linkUpdates;
    }
}
