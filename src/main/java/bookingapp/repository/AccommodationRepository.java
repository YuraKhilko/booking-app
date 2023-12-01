package bookingapp.repository;

import bookingapp.model.Accommodation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @EntityGraph(attributePaths = "amenities")
    Page<Accommodation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "amenities")
    Optional<Accommodation> findById(Long id);
}
