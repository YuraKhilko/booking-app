package bookingapp.mapper;

import bookingapp.config.MapperConfig;
import bookingapp.dto.accommodation.AccommodationDto;
import bookingapp.dto.accommodation.MergeAccommodationRequestDto;
import bookingapp.model.Accommodation;
import bookingapp.model.Amenity;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = AddressMapper.class)
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toEntity(AccommodationDto accommodationDto);

    Accommodation toEntity(MergeAccommodationRequestDto mergeAccommodationRequestDto);

    @AfterMapping
    default void setCategoryIds(
            @MappingTarget AccommodationDto accommodationDto, Accommodation accommodation) {
        Set<Long> amenityIdsSet = accommodation.getAmenities().stream()
                .map(a -> a.getId())
                .collect(Collectors.toSet());
        accommodationDto.setAmenityIds(amenityIdsSet);
    }

    @AfterMapping
    default void setCategories(
            @MappingTarget Accommodation accommodation,
            MergeAccommodationRequestDto mergeAccommodationRequestDto) {
        if (!mergeAccommodationRequestDto.getAmenityIds().isEmpty()) {
            Set<Amenity> amenitySet = mergeAccommodationRequestDto.getAmenityIds().stream()
                    .map(id -> new Amenity(id))
                    .collect(Collectors.toSet());
            accommodation.setAmenities(amenitySet);
        }
    }
}
