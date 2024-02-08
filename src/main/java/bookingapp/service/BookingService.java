package bookingapp.service;

import bookingapp.dto.booking.BookingDto;
import bookingapp.dto.booking.CreateBookingRequestDto;
import bookingapp.dto.booking.UpdateBookingRequestDto;
import bookingapp.model.Booking;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto findById(Long userId, Long bookingId);

    BookingDto createBooking(Long userId, CreateBookingRequestDto createBookingRequestDto);

    List<BookingDto> getBookings(Long userId);

    List<BookingDto> getBookings(Pageable pageable, Long userId, Booking.Status status);

    BookingDto updateBooking(Long userId, Long bookingId,
                             UpdateBookingRequestDto updateBookingRequestDto);

    void deleteById(Long userId, Long bookingId);

    Booking findUsersBookingById(Long userId, Long bookingId);

    void expireOldBookings();
}
