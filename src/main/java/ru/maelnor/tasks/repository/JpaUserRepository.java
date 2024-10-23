package ru.maelnor.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maelnor.tasks.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления сущностями {@link UserEntity} с использованием JPA.
 * Позволяет выполнять CRUD-операции и дополнительные запросы, связанные с пользователями.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Находит пользователя по его имени.
     *
     * @param username имя пользователя
     * @return {@link Optional}, содержащий сущность пользователя, если она найдена
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Проверяет, существует ли пользователь с указанным именем.
     *
     * @param username имя пользователя
     * @return {@code true}, если пользователь существует, иначе {@code false}
     */
    Boolean existsByUsername(String username);

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email электронная почта пользователя
     * @return {@code true}, если пользователь существует, иначе {@code false}
     */
    Boolean existsByEmail(String email);
}
