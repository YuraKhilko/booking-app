package bookingapp.service;

import bookingapp.dto.payment.CreatePaymentResponseDto;
import bookingapp.dto.payment.CreatePaymentResultDto;
import bookingapp.dto.payment.PaymentDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookingPaymentService {
    CreatePaymentResponseDto createPayment(Long userId, Long bookingId);

    CreatePaymentResultDto processSuccessPayment(Long paymentId);

    CreatePaymentResultDto processCancelPayment(Long paymentId);

    List<PaymentDto> getPayments(Pageable pageable, Long userIdToGetPayments);
}
