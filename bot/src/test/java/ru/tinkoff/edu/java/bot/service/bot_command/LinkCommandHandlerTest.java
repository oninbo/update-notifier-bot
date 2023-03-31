package ru.tinkoff.edu.java.bot.service.bot_command;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.configuration.Command;
import ru.tinkoff.edu.java.bot.dto.LinkResponse;
import ru.tinkoff.edu.java.bot.dto.ListLinksResponse;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.service.bot_command.handler.ListCommandHandler;
import ru.tinkoff.edu.java.link_parser.LinkParserService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(RandomBeansExtension.class)
public class LinkCommandHandlerTest {
    record Message(Long recipientId, String text) {
    }

    @Mock
    private ApplicationConfig applicationConfig;

    private List<Message> messages;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private UserResponseService userResponseService;

    private ListCommandHandler listCommandHandler;

    @Random
    private static Long RANDOM_LINK_ID1;

    @Random
    private static Long RANDOM_LINK_ID2;

    @Random
    private static Long RANDOM_USER_ID;

    @BeforeEach
    public void initialize() {
        messages = new ArrayList<>();

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            messages.add(new Message((Long) args[0], (String) args[1]));
            return null;
        }).when(userResponseService).sendMessage(anyLong(), anyString());

        when(applicationConfig.command()).thenReturn(mock(Command.class));
        when(applicationConfig.command().list()).thenReturn(mock(Command.List.class));

        var context = new AnnotationConfigApplicationContext(
                ru.tinkoff.edu.java.link_parser.configuration.ApplicationConfig.class
        );
        listCommandHandler = new ListCommandHandler(
                userResponseService,
                context.getBean(LinkParserService.class),
                applicationConfig,
                scrapperClient
        );
    }

    @Test
    public void shouldSendListOfLinks() throws URISyntaxException {
        var links = List.of(
                new LinkResponse(RANDOM_LINK_ID1, new URI("https://github.com/sanyarnd/tinkoff-java-course-2022")),
                new LinkResponse(RANDOM_LINK_ID2, new URI("https://stackoverflow.com/questions/42307687/"))
        );
        when(scrapperClient.getLinks(RANDOM_USER_ID))
                .thenReturn(new ListLinksResponse(links, links.size()));

        when(applicationConfig.command().list().header()).thenReturn("Отслеживаемые ссылки");

        listCommandHandler.handle(new BotCommandArguments(null, RANDOM_USER_ID));

        String text = String.join("\n",
                "Отслеживаемые ссылки:",
                "1. GitHub [репозиторий tinkoff-java-course-2022](https://github.com/sanyarnd/tinkoff-java-course-2022)" +
                        " пользователя sanyarnd",
                "2. [Вопрос](https://stackoverflow.com/questions/42307687/) на Stack Overflow"
        );

        assertIterableEquals(List.of(new Message(RANDOM_USER_ID, text + "\n")), messages);
    }

    @Test
    public void shouldSendMessageAboutEmptyList() {
        when(scrapperClient.getLinks(RANDOM_USER_ID))
                .thenReturn(new ListLinksResponse(List.of(), 0));

        String text = "У вас нет отслеживаемых ссылок";

        when(applicationConfig.command().list().message()).thenReturn(mock(Command.List.Message.class));
        when(applicationConfig.command().list().message().noLinks()).thenReturn(text);

        listCommandHandler.handle(new BotCommandArguments(null, RANDOM_USER_ID));

        assertIterableEquals(List.of(new Message(RANDOM_USER_ID, text)), messages);
    }
}
