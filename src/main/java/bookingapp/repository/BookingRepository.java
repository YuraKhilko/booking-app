package bookingapp.repository;

import bookingapp.exception.BookingManipulationException;
import bookingapp.model.Booking;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"user", "accommodation"})
    List<Booking> findAllByUserId(Long userId);

    @Transactional
    void deleteByUserIdAndId(Long userId, Long id);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN FETCH b.accommodation "
            + "INNER JOIN FETCH b.user "
            + "WHERE (:status IS NULL OR b.status = :status) "
            + "AND (:userId IS NULL OR b.user.id = :userId)")
    Page<Booking> findAllByUserIdAndStatus(Pageable pageable, Long userId, Booking.Status status);

    @EntityGraph(attributePaths = {"user", "accommodation", "accommodation.address"})
    Optional<Booking> findById(Long bookingId);

    @Transactional
    default boolean isNewBookingAvailableAndSave(Booking booking) {
        if (isBookingAvailable(booking)) {
            this.save(booking);
        }
        return true;
    }

    private boolean isBookingAvailable(Booking booking) {
        Period period = Period.between(booking.getCheckIn(), booking.getCheckOut());
        int totalBookingDays = period.getDays();
        for (int i = 0; i < totalBookingDays; i++) {
            LocalDate checkIn = booking.getCheckIn().plusDays(i);
            LocalDate checkOut = checkIn.plusDays(1);
            boolean isNewBookingAvailablePerDay = this.isNewBookingAvailablePerDay(
                    booking.getAccommodation().getId(),
                    checkIn,
                    checkOut,
                    booking.getId());
            if (!isNewBookingAvailablePerDay) {
                throw new BookingManipulationException("Booking with such parameters is "
                        + "not available.");
            }
        }
        return true;
    }

    @Query("SELECT CASE WHEN (count(1) < b.accommodation.availability "
            + "AND b.accommodation.isDeleted != TRUE) THEN TRUE ELSE FALSE END "
            + "FROM Booking b "
            + "WHERE :dateTo > b.checkIn "
            + "AND :dateFrom < b.checkOut "
            + "AND b.accommodation.id = :accommodationId "
            + "AND b.status != 'CANCELED' "
            + "AND (:bookingId IS NULL OR b.id != :bookingId)")
    boolean isNewBookingAvailablePerDay(
            Long accommodationId,
            LocalDate dateFrom,
            LocalDate dateTo,
            Long bookingId);
}
