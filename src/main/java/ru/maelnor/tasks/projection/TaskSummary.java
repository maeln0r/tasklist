package ru.maelnor.tasks.projection;

import ru.maelnor.tasks.entity.UserEntity;

import java.util.UUID;

/**
 * Projection for {@link ru.maelnor.tasks.entity.TaskEntity}
 */
public interface TaskSummary {
    UUID getId();
    String getName();
    boolean isCompleted();
    UserEntity getOwner();
}