package bookingapp.repository;

import bookingapp.model.Booking;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserIdAndId(Long userId, Long id);

    @Query("SELECT CASE WHEN count(1) < b.accommodation.availability THEN true ELSE false END "
            + "FROM Booking b "
            + "WHERE :dateTo > b.checkIn "
            + "AND :dateFrom < b.checkOut "
            + "AND b.accommodation.id = :accommodationId "
            + "AND b.status != 'CANCELED' "
            + "AND (:bookingId IS NULL OR b.id != :bookingId)")
    boolean checkIfNewBookingAvailable(
            Long accommodationId,
            LocalDate dateFrom,
            LocalDate dateTo,
            Long bookingId);

    @EntityGraph(attributePaths = {"user", "accommodation"})
    List<Booking> findAllByUserId(Long userId);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN FETCH b.accommodation "
            + "INNER JOIN FETCH b.user "
            + "WHERE (:status IS NULL OR b.status = :status) "
            + "AND (:userId IS NULL OR b.user.id = :userId)")
    Page<Booking> findAllByUserIdAndStatus(Pageable pageable, Long userId, Booking.Status status);

    @Transactional
    void deleteByUserIdAndId(Long userId, Long id);
}
