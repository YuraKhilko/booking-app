package bookingapp.telegram;

import bookingapp.model.Booking;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;

    @Override
    public boolean sendBookingNotification(Booking booking) {
        Long telegramUserId = booking.getUser().getTelegramChatId();
        if (telegramUserId != null) {
            String message = getMessage(booking);
            sendMessage(telegramUserId, message);
            return true;
        }
        return false;
    }

    private String getMessage(Booking booking) {
        String bookingStatus = "";
        switch (booking.getStatus()) {
            case PENDING:
                bookingStatus = "accepted";
                break;
            default:
                bookingStatus = booking.getStatus().name().toLowerCase();
        }

        String message = "Booking " + bookingStatus + ":" + "\n"
                + "check in: " + booking.getCheckIn() + ',' + "\n"
                + "check out: " + booking.getCheckOut() + '.' + "\n"
                + "Address: " + booking.getAccommodation().getAddress().getName() + '.' + "\n"
                + "Status: " + booking.getStatus();
        return message;
    }

    @Async
    public CompletableFuture<?> sendMessage(Long who, String what) {
        return CompletableFuture.runAsync(
                () -> telegramBot.send(new SendMessage(who.toString(), what)));
    }
}
