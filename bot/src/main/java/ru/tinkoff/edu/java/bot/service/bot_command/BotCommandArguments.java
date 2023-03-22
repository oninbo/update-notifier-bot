package ru.tinkoff.edu.java.bot.service.bot_command;

public record BotCommandArguments(
        String text,
        Long userId
) {
}
