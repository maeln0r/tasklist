package ru.maelnor.tasks.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.dto.filter.TaskFilter;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface TaskSpecification {

    static Specification<TaskEntity> withFilter(TaskFilter taskFilter) {
        return Stream.of(
                        Optional.ofNullable(taskFilter.getName())
                                .filter(name -> !name.isEmpty())
                                .map(TaskSpecification::byTaskName)
                                .orElse(null),
                        Optional.ofNullable(taskFilter.getCompleted())
                                .map(TaskSpecification::byCompleted)
                                .orElse(null),
                        Optional.ofNullable(taskFilter.getOwnerId())
                                .map(TaskSpecification::ownedBy)
                                .orElse(null)
                ).filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(Specification.where(null));
    }


    static Specification<TaskEntity> byTaskName(String taskName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + taskName + "%");
    }

    static Specification<TaskEntity> byCompleted(boolean completed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("completed"), completed);
    }

    static Specification<TaskEntity> ownedBy(UUID ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
