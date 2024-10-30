package ru.maelnor.tasks.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;

import java.util.UUID;

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
     * @param context       контекст валидатора
     * @return {@code true}, если поля не равны {@code null}, иначе {@code false}
     */
    @Override
    public boolean isValid(TaskFilterDto taskFilterDto, ConstraintValidatorContext context) {
        boolean valid = true;

        if (taskFilterDto.getPageNumber() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Номер страницы обязателен")
                    .addPropertyNode("pageNumber")
                    .addConstraintViolation();
            valid = false;
        }

        if (taskFilterDto.getPageSize() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Размер страницы обязателен")
                    .addPropertyNode("pageSize")
                    .addConstraintViolation();
            valid = false;
        }

        // Проверка формата userId как UUID
        if (!ObjectUtils.isEmpty(taskFilterDto.getOwnerId())) {
            try {
                UUID.fromString(taskFilterDto.getOwnerId());
            } catch (IllegalArgumentException e) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Некорректный формат UUID для поля ownerId")
                        .addPropertyNode("ownerId")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}
