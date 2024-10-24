<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />

<@base.layout>
    <h2>${pageTitle!''}</h2>

    <!-- Основная информация о задаче -->
    <div class="task-view-container">
        <div class="task-detail">
            <label>Имя задачи:</label>
            <p>${taskDto.name}</p>
        </div>

        <div class="task-detail">
            <label>Описание:</label>
            <p>${taskDto.description!}</p>
        </div>

        <div class="task-detail">
            <label>Статус:</label>
            <p class="${taskDto.completed?string("status-completed", "status-not-completed")}">
                ${taskDto.completed?string("Завершена", "Не завершена")}
            </p>
        </div>
    </div>

    <!-- Кнопки действий -->
    <div class="action-buttons">
        <a href="/tasks/edit/${taskDto.id}" class="btn btn-primary">Редактировать</a>
        <form action="/tasks/${taskDto.id}" method="post" style="display:inline;">
            <input type="hidden" name="_method" value="delete"/>
            <button type="submit" class="btn btn-danger">Удалить</button>
        </form>
        <a href="/tasks" class="btn btn-secondary">Назад к списку</a>
    </div>
</@base.layout>
