package bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Long accommodationId;
    private Long userId;
    private String status;
}
