package bookingapp.telegram;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final Dotenv dotenv = Dotenv.load();
    private final Map<String, Command> commands;
    private final Map<String, String> chatHistory = new ConcurrentHashMap<>();

    @Autowired
    public TelegramBot(List<Command> commandList) {
        this.commands = commandList.stream()
                .collect(Collectors.toMap(Command::getCommandName,
                        Function.identity()
                ));
    }

    @Override
    public String getBotUsername() {
        return dotenv.get("TELEGRAM_BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return dotenv.get("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (commands.containsKey(text)) {
                BotApiMethod message = commands.get(text).processCommand(update);
                chatHistory.put(chatId, text);
                send(message);
            } else if (chatHistory.containsKey(chatId)) {
                String commandString = chatHistory.get(chatId);
                BotApiMethod message = commands.get(commandString).processResponse(update);
                chatHistory.remove(chatId);
                send(message);
            }
        }
    }

    public void send(BotApiMethod message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            return;
        }
    }

}
