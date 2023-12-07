package bookingapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateAEarlierThanBValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DateAEarlierThanB {
    String message() default "Date A must be earlier than date B.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String a();
    String b();
}
