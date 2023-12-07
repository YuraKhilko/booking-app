package bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE amenities SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = FALSE")
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public Amenity(Long id) {
        this.id = id;
    }

    public Amenity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Amenity amenity = (Amenity) o;
        return Objects.equals(id, amenity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
