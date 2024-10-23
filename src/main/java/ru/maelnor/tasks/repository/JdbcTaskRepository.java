package ru.maelnor.tasks.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import ru.maelnor.tasks.entity.TaskEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория для работы с задачами с использованием JDBC.
 * Использует {@link JdbcTemplate} для выполнения операций с базой данных.
 * Репозиторий активируется, если свойство "repository.type" установлено в "jdbc".
 */
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc", matchIfMissing = true)
@Deprecated
public class JdbcTaskRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Конструктор, принимающий {@link JdbcTemplate} для работы с базой данных.
     *
     * @param jdbcTemplate объект для выполнения SQL-запросов
     */
    public JdbcTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Возвращает список всех задач из базы данных.
     *
     * @return список сущностей {@link TaskEntity}
     */
    @Override
    public List<TaskEntity> findAll() {
        String sql = "SELECT * FROM tasks";
        return jdbcTemplate.query(sql, taskRowMapper());
    }

    /**
     * Сохраняет новую задачу в базе данных.
     *
     * @param taskEntity сущность задачи для сохранения
     */
    @Override
    public void save(TaskEntity taskEntity) {
        String sql = "INSERT INTO tasks (name, completed) VALUES (?, ?)";
        jdbcTemplate.update(sql, taskEntity.getName(), taskEntity.isCompleted());
    }

    /**
     * Обновляет существующую задачу в базе данных.
     *
     * @param taskEntity сущность задачи для обновления
     */
    @Override
    public void update(TaskEntity taskEntity) {
        String sql = "UPDATE tasks SET name = ?, completed = ? WHERE id = ?";
        jdbcTemplate.update(sql, taskEntity.getName(), taskEntity.isCompleted(), taskEntity.getId());
    }

    /**
     * Удаляет задачу из базы данных по её идентификатору.
     *
     * @param id идентификатор задачи
     */
    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * Удаляет все задачи из базы данных.
     */
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM tasks";
        jdbcTemplate.update(sql);
    }

    /**
     * Находит задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return объект {@link Optional}, содержащий найденную задачу, если она существует
     */
    @Override
    public Optional<TaskEntity> findById(UUID id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        List<TaskEntity> taskEntities = jdbcTemplate.query(sql, taskRowMapper(), id);
        return taskEntities.stream().findFirst();
    }

    /**
     * Маппер строк для преобразования данных из результата запроса в объект {@link TaskEntity}.
     *
     * @return {@link RowMapper} для преобразования строк запроса
     */
    private RowMapper<TaskEntity> taskRowMapper() {
        return (rs, rowNum) -> {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(UUID.fromString(rs.getString("id")));
            taskEntity.setName(rs.getString("name"));
            taskEntity.setCompleted(rs.getBoolean("completed"));
            return taskEntity;
        };
    }
}
