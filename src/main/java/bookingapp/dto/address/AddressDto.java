package bookingapp.dto.address;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddressDto {
    @NotEmpty
    private String name;
}
