package bookingapp.dto.paymentsession;

import java.util.List;
import lombok.Data;

@Data
public class CreatePaymentSessionRequestDto {
    private String successUrl;
    private String cancelUrl;
    private List<PaymentElementDto> paymentElementDtoList;
    private String mode = "PAYMENT";
}
