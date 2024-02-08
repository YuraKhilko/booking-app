package bookingapp.repository;

import bookingapp.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p "
            + "INNER JOIN FETCH p.booking b "
            + "INNER JOIN FETCH b.user "
            + "WHERE b.user.id = :userId")
    Page<Payment> findAllByUserId(Pageable pageable, Long userId);
}
