package bookingapp.exception;

public class StripeException extends RuntimeException {
    public StripeException(String message) {
        super(message);
    }
}
