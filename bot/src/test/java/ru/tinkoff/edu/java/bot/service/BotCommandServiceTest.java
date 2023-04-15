package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.User;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.configuration.Command;
import ru.tinkoff.edu.java.bot.service.bot_command.handler.BotCommandHandler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(RandomBeansExtension.class)
public class BotCommandServiceTest {

    record Message(Long recipientId, String text) {
    }

    private List<BotCommandServiceTest.Message> messages;

    @SuppressWarnings("unused")
    @Mock
    private List<BotCommandHandler> botCommandHandlers;

    @Mock
    private UserResponseService userResponseService;

    @Mock
    private ApplicationConfig applicationConfig;

    @InjectMocks
    private BotCommandService botCommandService;

    @Random
    private Long RANDOM_USER_ID;

    @BeforeEach
    public void initialize() {
        messages = new ArrayList<>();

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            messages.add(new BotCommandServiceTest.Message((Long) args[0], (String) args[1]));
            return null;
        }).when(userResponseService).sendMessage(anyLong(), anyString());

        when(applicationConfig.command()).thenReturn(mock(Command.class));
        when(applicationConfig.command().common()).thenReturn(mock(Command.Common.class));
        when(applicationConfig.command().common().message()).thenReturn(mock(Command.Common.Message.class));
    }

    @Test
    public void shouldSendCommandNotSupported() {
        String text = "Команда не поддерживается";
        when(applicationConfig.command().common().message().unsupportedCommand()).thenReturn(text);

        var command = "/not_supported_command";

        var message = mock(com.pengrad.telegrambot.model.Message.class);
        when(message.text()).thenReturn(command);
        var user = mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(RANDOM_USER_ID);

        var messageEntity = mock(MessageEntity.class);
        when(messageEntity.offset()).thenReturn(0);
        when(messageEntity.length()).thenReturn(command.length());

        botCommandService.handleCommandEntity(message, messageEntity);

        assertIterableEquals(List.of(new Message(RANDOM_USER_ID, text)), messages);
    }
}
