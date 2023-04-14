package ru.tinkoff.edu.java.scrapper.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeAnswersResponse;
import ru.tinkoff.edu.java.scrapper.dto.ListStackExchangeQuestionsResponse;
import ru.tinkoff.edu.java.scrapper.dto.StackExchangeAnswerResponse;

public interface StackExchangeClient {
    /**
     * Фильтр, чтобы получать ссылку на ответ из API
     * @see StackExchangeAnswerResponse#link()
     * @see <a href="https://api.stackexchange.com/docs/filters">Custom Filters</a>
     */
    String ANSWER_LINK_FILTER = "!nOedRLj6*S";

    @GetExchange("/{version}/questions/{ids}")
    ListStackExchangeQuestionsResponse getStackExchangeQuestions(
            @PathVariable String version,
            @PathVariable String ids,
            @RequestParam(name = "site") String site
    );

    @GetExchange("/{version}/questions/{ids}/answers")
    ListStackExchangeAnswersResponse getStackExchangeAnswers(
            @PathVariable String version,
            @PathVariable String ids,
            @SuppressWarnings("SpellCheckingInspection") @RequestParam Long fromdate,
            @RequestParam String filter,
            @RequestParam(name = "site") String site,
            @RequestParam Integer page
    );
}
