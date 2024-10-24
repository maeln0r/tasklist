package ru.maelnor.tasks.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.maelnor.tasks.entity.RoleType;
import ru.maelnor.tasks.entity.UserEntity;
import ru.maelnor.tasks.model.UserModel;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {
    @Test
    void shouldMapEntityToModel() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername("username");
        userEntity.setEmail("email");
        userEntity.setEmail("password");
        userEntity.setRoles(new HashSet<>() {{
            add(RoleType.ROLE_USER);
        }});

        // Act
        UserModel userModel = UserMapper.INSTANCE.toModel(userEntity);


        // Assert
        assertNotNull(userModel);
        Assertions.assertEquals(userEntity.getId(), userModel.getId());
        Assertions.assertEquals(userEntity.getUsername(), userModel.getUsername());
        Assertions.assertEquals(userEntity.getEmail(), userModel.getEmail());
    }

}