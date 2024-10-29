package ru.maelnor.tasks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.maelnor.tasks.dto.filter.TaskFilterDto;
import ru.maelnor.tasks.model.TaskFilterModel;

/**
 * Интерфейс для преобразования (маппинга) между различными моделями данных.
 * Использует библиотеку MapStruct для автоматической генерации реализации мапперов.
 * Выполняет преобразования между сущностями, DTO и моделями фильтра задач.
 */
@Mapper
public interface TaskFilterMapper {
    TaskFilterMapper INSTANCE = Mappers.getMapper(TaskFilterMapper.class);

    /**
     * Преобразует DTO фильтра задач {@link TaskFilterDto} в модель фильтра задач {@link TaskFilterModel}.
     *
     * @param taskFilterDto DTO фильтра задач
     * @return модель фильтра задач
     */
    TaskFilterModel toModel(TaskFilterDto taskFilterDto);

    /**
     * Преобразует модель фильтра задач {@link TaskFilterDto} в DTO фильтра задач {@link TaskFilterModel}.
     *
     * @param taskFilterModel модель фильтра задач
     * @return DTO фильтра задач
     */
    TaskFilterDto toDto(TaskFilterModel taskFilterModel);
}
