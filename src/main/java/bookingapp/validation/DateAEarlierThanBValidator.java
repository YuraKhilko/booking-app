package bookingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.springframework.beans.BeanWrapperImpl;

public class DateAEarlierThanBValidator implements ConstraintValidator<DateAEarlierThanB, Object> {
    private String dateA;
    private String dateB;

    public void initialize(DateAEarlierThanB constraintAnnotation) {
        this.dateA = constraintAnnotation.a();
        this.dateB = constraintAnnotation.b();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {
        LocalDate dateAValue = (LocalDate) new BeanWrapperImpl(value)
                .getPropertyValue(dateA);
        LocalDate dateBValue = (LocalDate) new BeanWrapperImpl(value)
                .getPropertyValue(dateB);
        return dateAValue.isBefore(dateBValue);
    }
}
