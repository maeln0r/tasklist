package ru.maelnor.tasks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.maelnor.tasks.entity.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления сущностями {@link RefreshTokenEntity} с использованием JPA.
 * Позволяет выполнять CRUD-операции с токенами обновления (refresh tokens).
 */
@Repository
public interface JpaRefreshTokenRepository extends CrudRepository<RefreshTokenEntity, Long> {

    /**
     * Находит сущность {@link RefreshTokenEntity} по значению refresh токена.
     *
     * @param refreshToken значение refresh токена
     * @return {@link Optional}, содержащий сущность токена, если она найдена
     */
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    /**
     * Удаляет все refresh токены, связанные с пользователем, по его идентификатору.
     *
     * @param userId идентификатор пользователя
     */
    void deleteByUserId(UUID userId);
}
