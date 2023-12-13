package bookingapp.telegram;

import bookingapp.model.Booking;

public interface NotificationService {
    boolean sendBookingNotification(Booking booking);
}
