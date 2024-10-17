package ru.maelnor.tasks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.maelnor.tasks.entity.RefreshTokenEntity;

import java.util.Optional;

@Repository
public interface JpaRefreshTokenRepository extends CrudRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    void deleteByUserId(Long userId);
}
