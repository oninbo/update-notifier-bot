package ru.tinkoff.edu.java.bot.service.bot_command.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.configuration.ApplicationConfig;
import ru.tinkoff.edu.java.bot.service.UserResponseService;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommand;
import ru.tinkoff.edu.java.bot.service.bot_command.BotCommandArguments;
import ru.tinkoff.edu.java.bot.service.bot_command.HelpCommand;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements BotCommandHandler {
    private final UserResponseService userResponseService;
    private final ApplicationConfig applicationConfig;
    private final List<BotCommand> botCommands;

    @Override
    public void handle(BotCommandArguments arguments) {
        StringBuilder stringBuilder = new StringBuilder(applicationConfig.command().help().header() + ":\n");
        String formatString = "%d. %s /%s %s\n";
        for (int i = 0; i < botCommands.size(); i++) {
            var command = botCommands.get(i);
            String line = String.format(
                    formatString,
                    i + 1,
                    command.getDescription(applicationConfig),
                    command.getCommandName(),
                    command.getArguments().map(this::formatCommandArguments).orElse("")
            );
            stringBuilder.append(line);
        }
        String text = stringBuilder.toString();
        userResponseService.sendMessage(arguments.userId(), text);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand instanceof HelpCommand;
    }

    private String formatCommandArguments(String[] commandArguments) {
        return String.join(" ", commandArguments);
    }
}
