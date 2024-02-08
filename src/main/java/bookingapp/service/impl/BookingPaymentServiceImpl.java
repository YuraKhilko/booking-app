package bookingapp.service.impl;

import static bookingapp.model.Booking.Status.CONFIRMED;
import static bookingapp.model.Payment.Status.PAID;

import bookingapp.dto.payment.CreatePaymentResponseDto;
import bookingapp.dto.payment.CreatePaymentResultDto;
import bookingapp.dto.payment.PaymentDto;
import bookingapp.dto.paymentsession.CreatePaymentSessionRequestDto;
import bookingapp.dto.paymentsession.CreatePaymentSessionResponseDto;
import bookingapp.exception.EntityNotFoundException;
import bookingapp.mapper.PaymentMapper;
import bookingapp.mapper.PaymentSessionMapper;
import bookingapp.model.Booking;
import bookingapp.model.Payment;
import bookingapp.repository.PaymentRepository;
import bookingapp.service.BookingPaymentService;
import bookingapp.service.BookingService;
import bookingapp.stripe.StripeClient;
import bookingapp.telegram.NotificationService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingPaymentServiceImpl implements BookingPaymentService {
    private static final Payment.Status DEFAULT_STATUS = Payment.Status.PENDING;
    private final BookingService bookingService;
    private final StripeClient stripeClient;
    private final PaymentRepository paymentRepository;
    private final PaymentSessionMapper paymentSessionMapper;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    @Override
    public CreatePaymentResponseDto createPayment(Long userId, Long bookingId) {
        Booking booking = bookingService.findUsersBookingById(userId, bookingId);
        if (!isBookingActual(booking)) {
            throw new RuntimeException("Booking has been payed.");
        }
        Payment payment = initiateAndSaveNewPaymentByBooking(booking);
        CreatePaymentSessionRequestDto createPaymentSessionRequestDto =
                paymentSessionMapper.toDto(payment);
        CreatePaymentSessionResponseDto paymentSession =
                stripeClient.createPaymentSession(createPaymentSessionRequestDto);
        updatePaymentByPaymentSessionAndSave(payment, paymentSession);
        return paymentMapper.toCreatePaymentResponseDto(payment);
    }

    private void updatePaymentByPaymentSessionAndSave(
            Payment payment,
            CreatePaymentSessionResponseDto paymentSession
    ) {
        payment.setSessionId(paymentSession.sessionId());
        payment.setUrl(paymentSession.url());
        paymentRepository.save(payment);
    }

    private Payment initiateAndSaveNewPaymentByBooking(Booking booking) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(DEFAULT_STATUS);
        BigDecimal dailyRate = booking.getAccommodation().getDailyRate();
        long days = Period.between(booking.getCheckIn(), booking.getCheckOut()).getDays();
        payment.setAmountToPay(dailyRate.multiply(BigDecimal.valueOf(days)));
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    @Transactional
    public CreatePaymentResultDto processSuccessPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        Booking booking = payment.getBooking();
        if (isBookingActual(booking) && isPaymentSuccess(payment)) {
            payment.setStatus(PAID);
            booking.setStatus(CONFIRMED);
            paymentRepository.save(payment);
            notificationService.sendBookingNotification(booking);
            return new CreatePaymentResultDto("Payment success");
        }
        return new CreatePaymentResultDto("Payment unsuccess");
    }

    private boolean isBookingActual(Booking booking) {
        return booking.getStatus().equals(Booking.Status.PENDING);
    }

    private boolean isPaymentSuccess(Payment payment) {
        return payment.getStatus().equals(Payment.Status.PENDING)
                && stripeClient.isPaymentSuccessBySessionId(payment.getSessionId());
    }

    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(
                () -> new EntityNotFoundException("Can't find payment by id " + paymentId)
        );
    }

    @Override
    public CreatePaymentResultDto processCancelPayment(Long paymentId) {
        return new CreatePaymentResultDto("The payment can be made later");
    }

    @Override
    public List<PaymentDto> getPayments(Pageable pageable, Long userIdToGetPayments) {
        Page<Payment> paymentList;
        if (userIdToGetPayments == null) {
            paymentList = paymentRepository.findAll(pageable);
        } else {
            paymentList = paymentRepository.findAllByUserId(pageable, userIdToGetPayments);
        }
        return paymentList.stream()
                .map(paymentMapper::toPaymentDto)
                .collect(Collectors.toList());
    }
}
