package ru.tinkoff.edu.java.bot.service.bot_command;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.service.UserResponseService;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements BotCommandHandler {
    private final UserResponseService userResponseService;
    private final Environment environment;

    @Override
    public void handle(BotCommandArguments arguments) {
        StringBuilder stringBuilder = new StringBuilder("Доступные команды:\n");
        String formatString = "%d. %s /%s %s\n";
        for (var command : BotCommand.values()) {
            String line = String.format(
                    formatString,
                    command.ordinal() + 1,
                    command.getDescription(environment),
                    command.toString().toLowerCase(),
                    command.getArguments().map(this::formatCommandArguments).orElse("")
            );
            stringBuilder.append(line);
        }
        String text = stringBuilder.toString();
        userResponseService.sendMessage(arguments.userId(), text);
    }

    @Override
    public boolean canHandle(BotCommand botCommand) {
        return botCommand == BotCommand.HELP;
    }

    private String formatCommandArguments(String[] commandArguments) {
        return String.join(" ", commandArguments);
    }
}
