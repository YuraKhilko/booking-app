package bookingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private String status;
    private Long bookingId;
    private BigDecimal amountToPay;
}
