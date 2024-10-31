<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />
<#-- Импорт фрагментов -->
<#import "/fragments/filterForm.ftl" as filterFragments />
<#import "/fragments/pagination.ftl" as paginationFragments />

<@base.layout>
    <h2>${pageTitle!''}</h2>

    <!-- Форма фильтрации -->
    <@filterFragments.filterForm
    action="/tasks"
    taskFilterDto=taskFilter
    canAccessOwnerId=canAccessOwnerId!false
    userList=userList!
    />

    <!-- Таблица задач -->
    <table class="taskEntity-table task-table">
        <thead>
        <tr>
            <th>Имя</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <#if page?? && page.content?? && (page.content?size > 0)>
            <#list page.content as taskEntity>
                <tr>
                    <td><a href="/tasks/view/${taskEntity.id}" class="task-link">${taskEntity.name}</a></td>
                    <td>
                        <span class="${taskEntity.completed?string("status-completed", "status-not-completed")}">
                            ${taskEntity.completed?string("Завершена", "Не завершена")}
                        </span>
                    </td>
                    <td class="action-buttons">
                        <a href="/tasks/edit/${taskEntity.id}" class="btn btn-primary">Редактировать</a>
                        <form action="/tasks/${taskEntity.id}" method="post" style="display:inline;">
                            <input type="hidden" name="_method" value="delete"/>
                            <button type="submit" class="btn btn-danger">Удалить</button>
                        </form>
                    </td>
                </tr>
            </#list>
        <#else>
            <tr>
                <td colspan="3" class="no-tasks-message">Задачи не найдены</td>
            </tr>
        </#if>
        </tbody>
    </table>
    <#if page?? && page.content?? && (page.content?size > 0)>
        <!-- Пагинация -->
        <@paginationFragments.pagination
        currentPage=page.number!0
        totalPages=page.totalPages!1
        pageSize=page.size!10
        baseUrl="/tasks"
        queryParams="&name=${taskFilter.name!}&completed=${taskFilter.completed!}${(canAccessOwnerId!false)?string('&ownerId=${taskFilter.ownerId!}', '')}"
        />
    </#if>
</@base.layout>

