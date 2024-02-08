package bookingapp.stripe;

import bookingapp.dto.paymentsession.CreatePaymentSessionRequestDto;
import bookingapp.dto.paymentsession.CreatePaymentSessionResponseDto;
import bookingapp.dto.paymentsession.PaymentElementDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeClient {
    private static final String PAYMENT_SUCCESS_STATUS = "paid";

    @Autowired
    public StripeClient(@Value("${stripe.api.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public CreatePaymentSessionResponseDto createPaymentSession(
            CreatePaymentSessionRequestDto dto
    ) {
        SessionCreateParams.Builder builder = SessionCreateParams.builder();

        dto.getPaymentElementDtoList().stream()
                .map(this::createLineItem)
                .forEach(builder::addLineItem);

        SessionCreateParams params =
                builder
                        .setSuccessUrl(dto.getSuccessUrl())
                        .setCancelUrl(dto.getCancelUrl())
                        .setMode(SessionCreateParams.Mode.valueOf(dto.getMode()))
                        .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new bookingapp.exception.StripeException("Couldn't create Stripe session. " + e);
        }

        System.out.println(session.getUrl());

        return new CreatePaymentSessionResponseDto(session.getId(), session.getUrl());
    }

    private SessionCreateParams.LineItem createLineItem(PaymentElementDto dto) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(dto))
                .setQuantity(dto.getQuantity())
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(PaymentElementDto dto) {
        return new SessionCreateParams.LineItem.PriceData.Builder()
                .setCurrency(dto.getCurrency())
                .setProductData(createProductData(dto))
                .setUnitAmount(dto.getPriceInCents())
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(
            PaymentElementDto dto
    ) {
        return new SessionCreateParams.LineItem.PriceData.ProductData.Builder()
                .setName(dto.getName())
                .build();
    }

    public boolean isPaymentSuccessBySessionId(String sessionId) {
        try {
            Session session =
                    Session.retrieve(
                            sessionId
                    );
            return session.getPaymentStatus().equals(PAYMENT_SUCCESS_STATUS);
        } catch (StripeException e) {
            throw new bookingapp.exception.StripeException("Couldn't get Stripe session info "
                    + "by id " + sessionId + ". " + e);
        }
    }

}
