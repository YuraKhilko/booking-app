package bookingapp.telegram;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    String getCommandName();

    String getCommandDescription();

    BotApiMethod processCommand(Update update);

    BotApiMethod processResponse(Update update);
}
