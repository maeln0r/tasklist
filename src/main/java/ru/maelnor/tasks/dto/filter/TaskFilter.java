package ru.maelnor.tasks.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.maelnor.tasks.validation.TaskFilterValid;

@Data
@NoArgsConstructor
@TaskFilterValid
@EqualsAndHashCode
public class TaskFilter {
    private Integer pageNumber;
    private Integer pageSize;
    private String name;
    private Boolean completed;
}
