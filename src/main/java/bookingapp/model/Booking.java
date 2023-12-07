package bookingapp.model;

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
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE bookings SET status = 'CANCELED' WHERE id = ?")
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;
    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        CONFIRMED,
        CANCELED,
        EXPIRED
    }
}
