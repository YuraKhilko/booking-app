package bookingapp.mapper;

import bookingapp.config.MapperConfig;
import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import bookingapp.model.Accommodation;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = AddressMapper.class)
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toEntity(AccommodationDto accommodationDto);

    Accommodation toEntity(CreateAccommodationRequestDto createAccommodationRequestDto);
}
