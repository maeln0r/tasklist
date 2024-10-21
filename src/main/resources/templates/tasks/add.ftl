<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />

<@base.layout>
    <h2>${pageTitle}</h2>
    <form action="/tasks/add" method="post" class="taskEntity-form">

        <!-- Поле для имени задачи -->
        <div class="form-group">
            <label for="taskEntity-name">Имя задачи</label>
            <input type="text" name="name" id="taskEntity-name" placeholder="Введите имя задачи" class="form-control" value="${taskDto.name!}" />
            <#if bindingErrors?? && bindingErrors?has_content>
                <#list bindingErrors as error>
                    <div class="error-message">${error.defaultMessage}</div>
                </#list>
            </#if>
        </div>

        <!-- Чекбокс для отметки завершения задачи -->
        <div class="form-group">
            <label for="taskEntity-completed" class="checkbox-container">
                <input type="checkbox" name="completed" id="taskEntity-completed" <#if taskDto.completed>checked</#if> />
                <span>Завершена</span>
            </label>
        </div>

        <!-- Кнопка отправки формы -->
        <button type="submit" class="btn btn-primary">Добавить задачу</button>
    </form>
</@base.layout>
