package bookingapp.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
