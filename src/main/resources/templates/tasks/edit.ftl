<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />

<@base.layout>
    <h2>${pageTitle}</h2>
    <form action="/tasks/edit/${taskDto.id}" method="post" class="taskEntity-form task-form-container">
        <!-- Поле для имени задачи -->
        <input type="hidden" name="_method" value="put"/>

        <div class="form-group">
            <label for="taskEntity-name">Имя задачи</label>
            <input type="text" name="name" id="taskEntity-name" placeholder="Введите имя задачи" class="task-form-control" value="${taskDto.name!}" />
            <#if bindingErrors?? && bindingErrors?has_content>
                <#list bindingErrors as error>
                    <div class="error-message">${error.defaultMessage}</div>
                </#list>
            </#if>
        </div>

        <!-- Поле для описания задачи -->
        <div class="form-group">
            <label for="taskEntity-description">Текст задачи</label>
            <textarea name="description" id="taskEntity-description" placeholder="Введите текст задачи" class="task-form-control">${taskDto.description!}</textarea>
            <#if bindingErrors?? && bindingErrors?has_content>
                <#list bindingErrors as error>
                    <div class="error-message">${error.defaultMessage}</div>
                </#list>
            </#if>
        </div>

        <!-- Чекбокс для отметки завершения задачи -->
        <div class="form-group checkbox-group">
            <label for="taskEntity-completed" class="checkbox-container">
                <input type="checkbox" name="completed" id="taskEntity-completed" <#if taskDto.completed>checked</#if> />
                <span>Завершена</span>
            </label>
        </div>

        <!-- Кнопка обновления формы -->
        <button type="submit" class="btn btn-primary">Обновить задачу</button>
    </form>
</@base.layout>
