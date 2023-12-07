package bookingapp.dto.booking;

import bookingapp.validation.DateAEarlierThanB;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
@DateAEarlierThanB(a = "checkIn",
        b = "checkOut",
        message = "Date checkIn must be earlier than date checkOut.")
public class CreateBookingRequestDto {
    @NotNull
    @FutureOrPresent
    private LocalDate checkIn;
    @NotNull
    @FutureOrPresent
    private LocalDate checkOut;
    @NotNull
    private Long accommodationId;
}
