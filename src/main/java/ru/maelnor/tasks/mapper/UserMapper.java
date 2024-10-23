package ru.maelnor.tasks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.model.UserModel;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Преобразует сущность пользователя {@link UserEntity} в модель пользователя {@link UserModel}.
     *
     * @param entity сущность пользователя
     * @return модель пользователя
     */
    UserModel toModel(UserEntity entity);
}
