package bookingapp.telegram.impl;

import bookingapp.telegram.Command;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class InfoCommand implements Command {
    private final List<String> commandList;

    @Autowired
    public InfoCommand(List<Command> commandList) {
        this.commandList = commandList.stream()
                .map(x -> x.getCommandName() + " - " + x.getCommandDescription())
                .collect(Collectors.toList());
    }

    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public String getCommandDescription() {
        return "get list of bot commands";
    }

    @Override
    public BotApiMethod processCommand(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        StringBuilder response = new StringBuilder("Choose a command:\n");
        response.append(getCommandName()).append(" - ")
                .append(getCommandDescription()).append("\n");
        for (String command : commandList) {
            response.append(command).append("\n");
        }
        return new SendMessage(chatId, response.toString());
    }

    @Override
    public BotApiMethod processResponse(Update update) {
        return processCommand(update);
    }
}
