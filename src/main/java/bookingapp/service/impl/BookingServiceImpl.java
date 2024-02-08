package bookingapp.service.impl;

import bookingapp.dto.booking.BookingDto;
import bookingapp.dto.booking.CreateBookingRequestDto;
import bookingapp.dto.booking.UpdateBookingRequestDto;
import bookingapp.exception.BookingManipulationException;
import bookingapp.exception.EntityNotFoundException;
import bookingapp.mapper.BookingMapper;
import bookingapp.model.Booking;
import bookingapp.model.User;
import bookingapp.repository.BookingRepository;
import bookingapp.service.BookingService;
import bookingapp.telegram.NotificationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Booking.Status DEFAULT_STATUS = Booking.Status.PENDING;
    private static final Booking.Status STATUS_ALLOWED_TO_UPDATE = Booking.Status.PENDING;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking bookingById = findUsersBookingById(userId, bookingId);
        return bookingMapper.toDto(bookingById);
    }

    public Booking findUsersBookingById(Long userId, Long bookingId) {
        Booking bookingById = findBookingById(bookingId);
        if (!bookingById.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Can't find users booking by id " + bookingId);
        }
        return bookingById;
    }

    @Override
    @Scheduled(cron = "0 0 12 * * ?")
    public void expireOldBookings() {
        bookingRepository.expireOldBookings();
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by id " + bookingId));
    }

    @Override
    public BookingDto createBooking(Long userId, CreateBookingRequestDto createBookingRequestDto) {
        Booking booking = getNewBookingByDto(userId, createBookingRequestDto);

        bookingRepository.isNewBookingAvailableAndSave(booking);

        reinitializeBookingAndSendNotification(booking.getId());

        return bookingMapper.toDto(booking);
    }

    private Booking getNewBookingByDto(
            Long userId,
            CreateBookingRequestDto createBookingRequestDto) {
        Booking booking = bookingMapper.toEntity(createBookingRequestDto);
        User user = new User();
        user.setId(userId);
        booking.setUser(user);
        booking.setStatus(DEFAULT_STATUS);
        return booking;
    }

    private void reinitializeBookingAndSendNotification(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        notificationService.sendBookingNotification(booking);
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
        Booking updatedBooking =
                getUpdatedBookingByDto(userId, bookingId, updateBookingRequestDto);

        if (isBookingStatusAllowedToUpdateOrThrow(updatedBooking)) {
            bookingRepository.isNewBookingAvailableAndSave(updatedBooking);
            notificationService.sendBookingNotification(updatedBooking);
            return bookingMapper.toDto(updatedBooking);
        }
        return null;
    }

    Booking getUpdatedBookingByDto(Long userId,
                                   Long bookingId,
                                   UpdateBookingRequestDto updateBookingRequestDto) {
        Booking updatedBooking = findUsersBookingById(userId, bookingId);
        Booking sourceBooking = bookingMapper.toEntity(updateBookingRequestDto);
        updatedBooking.setCheckIn(sourceBooking.getCheckIn());
        updatedBooking.setCheckOut(sourceBooking.getCheckOut());
        return updatedBooking;
    }

    private boolean isBookingStatusAllowedToUpdateOrThrow(Booking booking) {
        if (!booking.getStatus().equals(STATUS_ALLOWED_TO_UPDATE)) {
            throw new BookingManipulationException(
                    "Booking status is not allowed to update entity.");
        }
        return true;
    }

    @Override
    public void deleteById(Long userId, Long id) {
        bookingRepository.deleteByUserIdAndId(userId, id);
        Booking booking = findBookingById(id);
        notificationService.sendBookingNotification(booking);
    }
}
