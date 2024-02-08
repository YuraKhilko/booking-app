package bookingapp.dto.paymentsession;

import lombok.Data;

@Data
public class PaymentElementDto {
    private String name;
    private String currency = "usd";
    private Long priceInCents;
    private Long quantity;
}
