package bookingapp.dto.accommodation;

import bookingapp.dto.address.AddressDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class CreateAccommodationRequestDto {
    @NotEmpty
    private String type;
    @NotNull
    private AddressDto address;
    @NotEmpty
    private String size;
    @NotNull
    private Set<Long> amenityIds;
    @NotNull
    @Min(0)
    private BigDecimal dailyRate;
    @NotNull
    private Integer availability;
}
