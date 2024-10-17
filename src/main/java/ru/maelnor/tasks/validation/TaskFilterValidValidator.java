package ru.maelnor.tasks.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ru.maelnor.tasks.dto.filter.TaskFilter;

public class TaskFilterValidValidator implements ConstraintValidator<TaskFilterValid, TaskFilter> {
    @Override
    public boolean isValid(TaskFilter taskFilter, ConstraintValidatorContext constraintValidatorContext) {
        return !ObjectUtils.anyNull(taskFilter.getPageNumber(), taskFilter.getPageSize());
    }
}
