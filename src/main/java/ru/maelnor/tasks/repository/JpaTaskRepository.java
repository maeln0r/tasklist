package ru.maelnor.tasks.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.maelnor.tasks.entity.TaskEntity;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для управления сущностями {@link TaskEntity} с использованием JPA.
 * Активируется, если свойство "repository.type" установлено в "jpa".
 * Позволяет выполнять CRUD-операции и строить запросы с помощью спецификаций.
 */
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa", matchIfMissing = true)
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID>, JpaSpecificationExecutor<TaskEntity> {

    /**
     * Находит список задач, принадлежащих конкретному пользователю по его идентификатору.
     *
     * @param id идентификатор владельца задачи
     * @return список задач, связанных с данным пользователем
     */
    List<TaskEntity> findByOwnerId(UUID id);
}
