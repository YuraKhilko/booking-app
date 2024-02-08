package bookingapp.mapper;

import bookingapp.dto.paymentsession.CreatePaymentSessionRequestDto;
import bookingapp.model.Payment;

public interface PaymentSessionMapper {
    CreatePaymentSessionRequestDto toDto(Payment payment);
}
