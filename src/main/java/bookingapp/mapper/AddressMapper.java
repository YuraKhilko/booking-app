package bookingapp.mapper;

import bookingapp.config.MapperConfig;
import bookingapp.dto.address.AddressDto;
import bookingapp.model.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toEntity(AddressDto addressDto);
}
