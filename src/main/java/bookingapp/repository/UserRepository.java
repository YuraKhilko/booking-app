package bookingapp.repository;

import bookingapp.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u INNER JOIN FETCH u.roles r "
            + "WHERE u.email = :email "
            + "AND u.isDeleted = FALSE "
            + "AND r.isDeleted = FALSE")
    Optional<User> findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(Long userId);
}
