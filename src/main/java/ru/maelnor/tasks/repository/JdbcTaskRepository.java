package ru.maelnor.tasks.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import ru.maelnor.tasks.entity.TaskEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc", matchIfMissing = true)
@Deprecated
public class JdbcTaskRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TaskEntity> findAll() {
        String sql = "SELECT * FROM tasks";
        return jdbcTemplate.query(sql, taskRowMapper());
    }


    @Override
    public void save(TaskEntity taskEntity) {
        String sql = "INSERT INTO tasks (name, completed) VALUES (?, ?)";
        jdbcTemplate.update(sql, taskEntity.getName(), taskEntity.isCompleted());
    }

    @Override
    public void update(TaskEntity taskEntity) {
        String sql = "UPDATE tasks SET name = ?, completed = ? WHERE id = ?";
        jdbcTemplate.update(sql, taskEntity.getName(), taskEntity.isCompleted(), taskEntity.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<TaskEntity> findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        List<TaskEntity> taskEntities = jdbcTemplate.query(sql, taskRowMapper(), id);
        return taskEntities.stream().findFirst();
    }

    private RowMapper<TaskEntity> taskRowMapper() {
        return (rs, rowNum) -> {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(rs.getLong("id"));
            taskEntity.setName(rs.getString("name"));
            taskEntity.setCompleted(rs.getBoolean("completed"));
            return taskEntity;
        };
    }
}
