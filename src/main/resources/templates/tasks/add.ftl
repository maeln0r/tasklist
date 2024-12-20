<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />

<@base.layout>
    <h2>${pageTitle!''}</h2>
    <form action="/tasks/add" method="post" class="taskEntity-form task-form-container">

        <!-- Поле для имени задачи -->
        <div class="form-group">
            <label for="taskEntity-name">Имя задачи</label>
            <input
                    type="text"
                    name="name"
                    id="taskEntity-name"
                    placeholder="Введите имя задачи"
                    class="task-form-control<#if bindingErrors?? && bindingErrors.name??> error</#if>"
                    value="${taskDto.name!}" />
            <#if bindingErrors?? && bindingErrors.name??>
                <div class="error-message">${bindingErrors.name}</div>
            </#if>
        </div>

        <!-- Поле для описания задачи -->
        <div class="form-group">
            <label for="taskEntity-description">Текст задачи</label>
            <textarea
                    name="description"
                    id="taskEntity-description"
                    placeholder="Введите текст задачи"
                    class="task-form-control<#if bindingErrors?? && bindingErrors.description??> error</#if>">${taskDto.description!}</textarea>
            <#if bindingErrors?? && bindingErrors.description??>
                <div class="error-message">${bindingErrors.description}</div>
            </#if>
        </div>

        <!-- Чекбокс для отметки завершения задачи -->
        <div class="form-group checkbox-group">
            <label for="taskEntity-completed" class="checkbox-container">
                <input type="checkbox" name="completed" id="taskEntity-completed" <#if taskDto.completed>checked</#if> />
                <span>Завершена</span>
            </label>
        </div>

        <!-- Кнопка отправки формы -->
        <button type="submit" class="btn btn-primary">Добавить задачу</button>
    </form>
</@base.layout>
