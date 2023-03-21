package ru.tinkoff.edu.java.bot.service.bot_command;

public enum BotCommand {
    START, // зарегистрировать пользователя
    HELP, // вывести окно с командами
    TRACK, // начать отслеживание ссылки
    @SuppressWarnings("SpellCheckingInspection")
    UNTRACK, // прекратить отслеживание ссылки
    LIST // показать список отслеживаемых ссылок
}
