package ru.tinkoff.edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class BotUpdatesListener implements UpdatesListener {
    private final Logger logger;
    private final BotCommandService botCommandService;
    private final UserResponseService userResponseService;

    public BotUpdatesListener(BotCommandService botCommandService, UserResponseService userResponseService) {
        this.userResponseService = userResponseService;
        this.botCommandService = botCommandService;
        logger = LoggerFactory.getLogger(BotUpdatesListener.class);
    }

    @Override
    public int process(List<Update> updates) {
        for (var update : updates) {
            try {
                processUpdate(update);
            } catch (Exception exception) {
                logger.error(exception.toString());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        logger.info(update.toString());
        var message = update.message();
        Optional.ofNullable(message)
                .map(Message::entities)
                .ifPresent(messageEntities ->
                        Arrays.stream(messageEntities)
                                .forEach(entity -> processMessageEntity(entity, message))
                );
    }

    private void processMessageEntity(MessageEntity messageEntity, Message message) {
        if (messageEntity.type() == MessageEntity.Type.bot_command) {
            String command = message.text().substring(
                    messageEntity.offset(),
                    messageEntity.offset() + messageEntity.length()
            );
            String arguments = message.text().replace(command, "");
            botCommandService.handleCommand(command, arguments, userResponseService.to(message.from()));
        }
    }
}
