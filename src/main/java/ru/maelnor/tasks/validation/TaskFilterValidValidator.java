package ru.maelnor.tasks.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ru.maelnor.tasks.dto.filter.TaskFilter;

/**
 * Валидатор для аннотации {@link TaskFilterValid}.
 * Проверяет, что поля пагинации {@code pageNumber} и {@code pageSize} в {@link TaskFilter} не равны {@code null}.
 */
public class TaskFilterValidValidator implements ConstraintValidator<TaskFilterValid, TaskFilter> {

    /**
     * Проверяет валидность объекта {@link TaskFilter}.
     * Убедитесь, что оба поля {@code pageNumber} и {@code pageSize} заданы.
     *
     * @param taskFilter объект фильтра {@link TaskFilter}, который нужно проверить
     * @param constraintValidatorContext контекст валидатора
     * @return {@code true}, если поля не равны {@code null}, иначе {@code false}
     */
    @Override
    public boolean isValid(TaskFilter taskFilter, ConstraintValidatorContext constraintValidatorContext) {
        return !ObjectUtils.anyNull(taskFilter.getPageNumber(), taskFilter.getPageSize());
    }
}
