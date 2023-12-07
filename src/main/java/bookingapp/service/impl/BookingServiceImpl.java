package bookingapp.service.impl;

import bookingapp.dto.booking.BookingDto;
import bookingapp.dto.booking.CreateBookingRequestDto;
import bookingapp.dto.booking.UpdateBookingRequestDto;
import bookingapp.exception.DuplicateEntityException;
import bookingapp.exception.EntityNotFoundException;
import bookingapp.mapper.BookingMapper;
import bookingapp.model.Booking;
import bookingapp.model.User;
import bookingapp.repository.BookingRepository;
import bookingapp.service.AccommodationService;
import bookingapp.service.BookingService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Booking.Status DEFAULT_STATUS = Booking.Status.PENDING;
    private static final Booking.Status STATUS_ALLOWED_TO_UPDATE = Booking.Status.PENDING;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationService accommodationService;

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking bookingById = findBookingById(userId, bookingId);
        return bookingMapper.toDto(bookingById);
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, CreateBookingRequestDto createBookingRequestDto) {
        Booking booking = bookingMapper.toEntity(createBookingRequestDto);
        User user = initializeUserById(userId);
        booking.setUser(user);
        booking.setStatus(DEFAULT_STATUS);

        if (isBookingAvailable(booking)) {
            bookingRepository.save(booking);
            return bookingMapper.toDto(booking);
        }
        return null;
    }

    private User initializeUserById(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    private boolean isBookingAvailable(Booking booking) {
        Long accommodationId = booking.getAccommodation().getId();
        return isAccommodationAvailable(accommodationId) && isBookingNotPresent(booking);
    }

    private boolean isAccommodationAvailable(Long accommodationId) {
        return accommodationService.isAccommodationAvailable(accommodationId);
    }

    private boolean isBookingNotPresent(Booking booking) {
        Period period = Period.between(booking.getCheckIn(), booking.getCheckOut());
        int totalBookingDays = period.getDays();
        for (int i = 0; i < totalBookingDays; i++) {
            LocalDate checkIn = booking.getCheckIn().plusDays(i);
            LocalDate checkOut = checkIn.plusDays(1);
            boolean isBookingByOneDayAvailable = bookingRepository.checkIfNewBookingAvailable(
                    booking.getAccommodation().getId(),
                    checkIn,
                    checkOut,
                    booking.getId());
            if (!isBookingByOneDayAvailable) {
                throw new DuplicateEntityException("Booking with such parameters is "
                        + "already present.");
            }
        }
        return true;
    }

    @Override
    public List<BookingDto> getBookings(Long userId) {
        List<Booking> bookingList = bookingRepository.findAllByUserId(userId);
        return bookingList.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookings(Pageable pageable, Long userId, Booking.Status status) {
        Page<Booking> bookingsByUserIdAndStatus =
                bookingRepository.findAllByUserIdAndStatus(pageable, userId, status);
        return bookingsByUserIdAndStatus.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto updateBooking(
            Long userId,
            Long bookingId,
            UpdateBookingRequestDto updateBookingRequestDto
    ) {
        Booking bookingForUpdate = findBookingById(userId, bookingId);

        if (isBookingAllowedToUpdate(bookingForUpdate)) {
            Booking sourceBooking = bookingMapper.toEntity(updateBookingRequestDto);
            bookingForUpdate.setCheckIn(sourceBooking.getCheckIn());
            bookingForUpdate.setCheckOut(sourceBooking.getCheckOut());
            if (isBookingAvailable(bookingForUpdate)) {
                bookingRepository.save(bookingForUpdate);
                return bookingMapper.toDto(bookingForUpdate);
            }
        }
        return null;
    }

    private boolean isBookingAllowedToUpdate(Booking bookingForUpdate) {
        return isAccommodationAvailable(bookingForUpdate.getAccommodation().getId())
                && bookingForUpdate.getStatus().name().equals(STATUS_ALLOWED_TO_UPDATE.name());
    }

    private Booking findBookingById(Long userId, Long bookingId) {
        return bookingRepository.findByUserIdAndId(userId, bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id " + bookingId));
    }

    @Override
    public void deleteById(Long userId, Long id) {
        bookingRepository.deleteByUserIdAndId(userId, id);
    }
}
