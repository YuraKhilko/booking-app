package bookingapp.mapper;

import bookingapp.config.MapperConfig;
import bookingapp.dto.booking.BookingDto;
import bookingapp.dto.booking.CreateBookingRequestDto;
import bookingapp.dto.booking.UpdateBookingRequestDto;
import bookingapp.model.Accommodation;
import bookingapp.model.Booking;
import bookingapp.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    BookingDto toDto(Booking booking);

    Booking toEntity(BookingDto bookingDto);

    Booking toEntity(CreateBookingRequestDto createBookingRequestDto);

    Booking toEntity(UpdateBookingRequestDto updateBookingRequestDto);

    @AfterMapping
    default void setBookingFields(@MappingTarget Booking booking, BookingDto bookingDto) {
        User user = initializeUserById(bookingDto.getUserId());
        booking.setUser(user);
        Accommodation accommodation = initializeAccommodationById(bookingDto.getAccommodationId());
        booking.setAccommodation(accommodation);
    }

    @AfterMapping
    default void setBookingFields(
            @MappingTarget Booking booking,
            CreateBookingRequestDto createBookingRequestDto) {
        Accommodation accommodation =
                initializeAccommodationById(createBookingRequestDto.getAccommodationId());
        booking.setAccommodation(accommodation);
    }

    private User initializeUserById(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    private Accommodation initializeAccommodationById(Long accommodationId) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        return accommodation;
    }
}
