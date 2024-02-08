package bookingapp.repository;

import bookingapp.model.Accommodation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @Query("FROM Accommodation a "
            + "LEFT JOIN FETCH a.amenities "
            + "INNER JOIN FETCH a.address "
            + "WHERE a.isDeleted = FALSE")
    Page<Accommodation> findAll(Pageable pageable);

    @Query("FROM Accommodation a "
            + "LEFT JOIN FETCH a.amenities "
            + "INNER JOIN FETCH a.address "
            + "WHERE a.isDeleted = FALSE "
            + "AND a.id = :id")
    Optional<Accommodation> findById(Long id);
}
