package ru.maelnor.tasks.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.maelnor.tasks.dto.filter.TaskFilter;

import java.lang.annotation.*;

/**
 * Аннотация для валидации фильтра задач {@link TaskFilter}.
 * Проверяет, что поля пагинации указаны корректно.
 * Аннотация должна быть применена на уровне класса.
 */
@Documented
@Constraint(validatedBy = TaskFilterValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskFilterValid {

    /**
     * Сообщение об ошибке, если проверка не пройдена.
     *
     * @return сообщение об ошибке
     */
    String message() default "Поля пагинации должны быть указаны";

    /**
     * Определяет группы валидации для аннотации.
     *
     * @return массив классов групп
     */
    Class<?>[] groups() default {};

    /**
     * Определяет тип полезной нагрузки для клиентов аннотации.
     *
     * @return массив классов полезной нагрузки
     */
    Class<? extends Payload>[] payload() default {};
}
