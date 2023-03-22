package ru.tinkoff.edu.java.bot.service.bot_command;

import org.springframework.core.env.Environment;

import java.util.Optional;

public enum BotCommand {
    START, // зарегистрировать пользователя
    HELP, // вывести окно с командами
    TRACK { // начать отслеживание ссылки
        @Override
        public Optional<String[]> getArguments() {
            return Optional.of(new String[]{"link"});
        }
    },
    @SuppressWarnings("SpellCheckingInspection")
    UNTRACK { // прекратить отслеживание ссылки
        @Override
        public Optional<String[]> getArguments() {
            return Optional.of(new String[]{"link"});
        }
    },
    LIST; // показать список отслеживаемых ссылок

    public String getDescription(Environment environment) {
        return environment.getProperty(String.format("command.%s.description", toString().toLowerCase()));
    }

    public Optional<String[]> getArguments() {
        return Optional.empty();
    }
}
