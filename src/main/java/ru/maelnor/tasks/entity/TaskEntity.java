package ru.maelnor.tasks.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Сущность для хранения данных о задачах в базе данных.
 * Содержит информацию о задаче, включая её статус, описание, временные метки
 * создания и обновления, а также связь с владельцем задачи.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private boolean completed;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    /**
     * Метод, который автоматически устанавливает дату создания задачи перед сохранением в базу данных.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    /**
     * Метод, который автоматически обновляет дату обновления задачи перед её изменением в базе данных.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
