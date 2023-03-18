package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;

public interface StackExchangeClient {
    @GetExchange("/{version}/questions/{ids}")
    ListStackExchangeQuestionsResponse getStackExchangeQuestions(
            @PathVariable String version,
            @PathVariable String ids,
            @RequestParam(name = "site") String site
    );
}
