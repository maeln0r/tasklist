package ru.maelnor.tasks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.maelnor.tasks.dto.TaskDto;
import ru.maelnor.tasks.entity.TaskEntity;
import ru.maelnor.tasks.model.TaskModel;

/**
 * Интерфейс для преобразования (маппинга) между различными моделями данных.
 * Использует библиотеку MapStruct для автоматической генерации реализации мапперов.
 * Выполняет преобразования между сущностями, DTO и моделями задачи.
 */
@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    /**
     * Преобразует сущность задачи {@link TaskEntity} в DTO задачи {@link TaskDto}.
     *
     * @param taskEntity сущность задачи
     * @return DTO задачи
     */
    TaskDto toDto(TaskEntity taskEntity);

    /**
     * Преобразует модель задачи {@link TaskModel} в DTO задачи {@link TaskDto}.
     *
     * @param taskModel модель задачи
     * @return DTO задачи
     */
    TaskDto toDto(TaskModel taskModel);

    /**
     * Преобразует DTO задачи {@link TaskDto} в сущность задачи {@link TaskEntity}.
     * Игнорирует поля createdAt, updatedAt и owner, так как они не передаются в DTO.
     *
     * @param taskDto DTO задачи
     * @return сущность задачи
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    TaskEntity toEntity(TaskDto taskDto);

    /**
     * Преобразует сущность задачи {@link TaskEntity} в модель задачи {@link TaskModel}.
     *
     * @param taskEntity сущность задачи
     * @return модель задачи
     */
    TaskModel toModel(TaskEntity taskEntity);
}
