package ru.maelnor.tasks.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.model.TaskFilterModel;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Интерфейс, содержащий спецификации для фильтрации сущностей {@link TaskEntity}.
 * Позволяет строить динамические запросы к базе данных на основе параметров фильтрации.
 */
public interface TaskSpecification {

    /**
     * Создает спецификацию для фильтрации задач на основе параметров из {@link TaskFilterModel}.
     * Комбинирует условия фильтрации по имени задачи, статусу завершенности и идентификатору владельца.
     *
     * @param taskFilterModel объект {@link TaskFilterModel}, содержащий параметры фильтрации
     * @return спецификация для фильтрации задач
     */
    static Specification<TaskEntity> withFilter(TaskFilterModel taskFilterModel) {
        return Stream.of(
                        Optional.ofNullable(taskFilterModel.getName())
                                .filter(name -> !name.isEmpty())
                                .map(TaskSpecification::byTaskName)
                                .orElse(null),
                        Optional.ofNullable(taskFilterModel.getCompleted())
                                .map(TaskSpecification::byCompleted)
                                .orElse(null),
                        Optional.ofNullable(taskFilterModel.getOwnerId())
                                .map(TaskSpecification::ownedBy)
                                .orElse(null)
                ).filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(Specification.where(null));
    }

    /**
     * Создает спецификацию для фильтрации задач по имени.
     *
     * @param taskName имя задачи для поиска
     * @return спецификация, проверяющая соответствие имени задачи
     */
    static Specification<TaskEntity> byTaskName(String taskName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + taskName + "%");
    }

    /**
     * Создает спецификацию для фильтрации задач по статусу завершенности.
     *
     * @param completed статус завершенности задачи
     * @return спецификация, проверяющая соответствие статуса завершенности
     */
    static Specification<TaskEntity> byCompleted(boolean completed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("completed"), completed);
    }

    /**
     * Создает спецификацию для фильтрации задач по идентификатору владельца.
     *
     * @param ownerId идентификатор владельца задачи
     * @return спецификация, проверяющая соответствие идентификатора владельца
     */
    static Specification<TaskEntity> ownedBy(UUID ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
