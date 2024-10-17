package ru.maelnor.tasks.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TaskFilterValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskFilterValid {
    String message() default "Поля пагинации должы быть указаны";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
