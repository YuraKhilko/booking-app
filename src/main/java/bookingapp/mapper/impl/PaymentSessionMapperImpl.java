package bookingapp.mapper.impl;

import bookingapp.dto.paymentsession.CreatePaymentSessionRequestDto;
import bookingapp.dto.paymentsession.PaymentElementDto;
import bookingapp.mapper.PaymentSessionMapper;
import bookingapp.model.Booking;
import bookingapp.model.Payment;
import java.math.BigDecimal;
import java.time.Period;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentSessionMapperImpl implements PaymentSessionMapper {
    private static final String PAYMENT_DOMAIN = "/api/payments/";
    @Value("${success.payment.session.url}")
    private String successUrl;
    @Value("${cancel.payment.session.url}")
    private String cancelUrl;

    @Override
    public CreatePaymentSessionRequestDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        CreatePaymentSessionRequestDto dto = new CreatePaymentSessionRequestDto();
        dto.setSuccessUrl(successUrl + PAYMENT_DOMAIN + "success/" + payment.getId());
        dto.setCancelUrl(cancelUrl + PAYMENT_DOMAIN + "cancel/" + payment.getId());
        Booking booking = payment.getBooking();
        PaymentElementDto paymentElementDto = new PaymentElementDto();
        String paymentName = "Booking payment: "
                + "check in: " + booking.getCheckIn() + ", "
                + "check out: " + booking.getCheckOut() + ". "
                + "Address: " + booking.getAccommodation().getAddress().getName() + '.';
        paymentElementDto.setName(paymentName);
        long dailyRateInCents = booking.getAccommodation().getDailyRate()
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        paymentElementDto.setPriceInCents(dailyRateInCents);
        long days = Period.between(booking.getCheckIn(), booking.getCheckOut()).getDays();
        paymentElementDto.setQuantity(days);
        dto.setPaymentElementDtoList(List.of(paymentElementDto));
        return dto;
    }
}
