package ru.maelnor.tasks.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.maelnor.tasks.entity.TaskEntity;

import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID>, JpaSpecificationExecutor<TaskEntity> {
    List<TaskEntity> findByOwnerId(UUID id);
}