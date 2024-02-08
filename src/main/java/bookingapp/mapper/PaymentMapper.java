package bookingapp.mapper;

import bookingapp.config.MapperConfig;
import bookingapp.dto.payment.CreatePaymentResponseDto;
import bookingapp.dto.payment.PaymentDto;
import bookingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    CreatePaymentResponseDto toCreatePaymentResponseDto(Payment payment);

    @Mapping(source = "booking.id", target = "bookingId")
    PaymentDto toPaymentDto(Payment payment);
}
