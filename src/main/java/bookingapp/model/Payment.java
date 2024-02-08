package bookingapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @Column(name = "url")
    private String url;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "amount_to_pay", nullable = false)
    private BigDecimal amountToPay;

    public enum Status {
        PENDING,
        PAID
    }
}
