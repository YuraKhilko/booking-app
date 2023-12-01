package bookingapp.dto.accommodation;

import bookingapp.dto.address.AddressDto;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class AccommodationDto {
    private Long id;
    private String type;
    private AddressDto address;
    private String size;
    private Set<Long> amenityIds;
    private BigDecimal dailyRate;
    private Integer availability;
}
