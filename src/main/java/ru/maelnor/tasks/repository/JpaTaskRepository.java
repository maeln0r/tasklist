package ru.maelnor.tasks.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.projection.TaskSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления сущностями {@link TaskEntity} с использованием JPA.
 * Активируется, если свойство "repository.type" установлено в "jpa".
 * Позволяет выполнять CRUD-операции и строить запросы с помощью спецификаций.
 * todo: Найти как использовать проекции в запросах со спецификациями
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
    List<TaskSummary> findByOwnerId(UUID id);

    /**
     * Находит список всех задач с использованием проекции {@link TaskSummary}.
     *
     * @return список задач в виде проекции {@link TaskSummary}
     */
    List<TaskSummary> findAllProjectedBy();

    /**
     * Находит задачу по идентификатору с использованием проекции {@link TaskSummary}.
     *
     * @param id идентификатор задачи
     * @return задача, завернутая в Optional, в виде проекции {@link TaskSummary}
     */
    Optional<TaskSummary> findProjectedById(UUID id);
}
