package bookingapp.telegram.impl;

import bookingapp.model.User;
import bookingapp.repository.UserRepository;
import bookingapp.telegram.Command;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BookingNotificationsStart implements Command {
    private final UserRepository userRepository;

    @Override
    public String getCommandName() {
        return "/getBookingNotifications";
    }

    @Override
    public String getCommandDescription() {
        return "get Booking Notifications";
    }

    @Override
    public BotApiMethod processCommand(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String response = "Enter your registration email";
        return new SendMessage(chatId, response.toString());
    }

    @Override
    public BotApiMethod processResponse(Update update) {
        String response = "";
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String email = message.getText().toLowerCase();
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setTelegramChatId(chatId);
                userRepository.save(user);
                response = "Completed! Bookings news will be sent to you.";
            } else {
                response = "User with email " + email + " not found";
            }
        } catch (Exception e) {
            response = "User with email " + email + " not found";
        }
        return new SendMessage(chatId.toString(), response);
    }
}
