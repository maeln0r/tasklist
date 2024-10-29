package ru.maelnor.tasks.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;

/**
 * Валидатор для аннотации {@link TaskFilterValid}.
 * Проверяет, что поля пагинации {@code pageNumber} и {@code pageSize} в {@link TaskFilterDto} не равны {@code null}.
 */
public class TaskFilterValidValidator implements ConstraintValidator<TaskFilterValid, TaskFilterDto> {

    /**
     * Проверяет валидность объекта {@link TaskFilterDto}.
     * Убедитесь, что оба поля {@code pageNumber} и {@code pageSize} заданы.
     *
     * @param taskFilterDto объект фильтра {@link TaskFilterDto}, который нужно проверить
     * @param constraintValidatorContext контекст валидатора
     * @return {@code true}, если поля не равны {@code null}, иначе {@code false}
     */
    @Override
    public boolean isValid(TaskFilterDto taskFilterDto, ConstraintValidatorContext constraintValidatorContext) {
        return !ObjectUtils.anyNull(taskFilterDto.getPageNumber(), taskFilterDto.getPageSize());
    }
}
