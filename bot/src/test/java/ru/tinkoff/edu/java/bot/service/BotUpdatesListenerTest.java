package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.tinkoff.edu.java.bot.client.ScrapperClient;
import ru.tinkoff.edu.java.bot.dto.AddLinkRequest;
import ru.tinkoff.edu.java.bot.dto.LinkResponse;
import ru.tinkoff.edu.java.bot.dto.ListLinksResponse;
import ru.tinkoff.edu.java.bot.dto.RemoveLinkRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
public class BotUpdatesListenerTest {
    public final int size = 200;

    @Random(type = Update.class, size = size)
    private List<Update> updateList;

    @MockBean
    private ScrapperClient scrapperClient;

    @MockBean
    private TgBot tgBot;

    @Random(type = SendResponse.class, size = size)
    private List<SendResponse> responses;

    @Autowired
    private BotUpdatesListener botUpdatesListener;

    @BeforeEach
    public void initialize() {
        List<LinkResponse> linkResponses = List.of(mock(LinkResponse.class), mock(LinkResponse.class));
        when(scrapperClient.getLinks(anyLong())).thenReturn(new ListLinksResponse(linkResponses, linkResponses.size()));
        when(scrapperClient.addLink(anyLong(), any(AddLinkRequest.class))).thenReturn(linkResponses.get(0));
        when(scrapperClient.deleteLink(anyLong(), any(RemoveLinkRequest.class))).thenReturn(linkResponses.get(0));
        doNothing().when(scrapperClient).addTgChat(anyLong());
        var iterator = responses.iterator();
        when(tgBot.execute(any())).thenAnswer(args -> iterator.next());
    }

    @Test
    public void shouldHandleAnyUpdate() {
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, botUpdatesListener.process(updateList));
    }
}
