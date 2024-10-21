<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />
<#-- Импорт фрагментов -->
<#import "/fragments/filterForm.ftl" as filterFragments />
<#import "/fragments/pagination.ftl" as paginationFragments />

<@base.layout>
    <h2>${pageTitle}</h2>

    <!-- Форма фильтрации -->
    <@filterFragments.filterForm action="/tasks" taskFilter=taskFilter />

    <!-- Таблица задач -->
    <table class="taskEntity-table">
        <thead>
        <tr>
            <th>Имя</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <#list page.content as taskEntity>
            <tr>
                <td>${taskEntity.name}</td>
                <td>
                    <span class="${taskEntity.completed?string("status-completed", "status-not-completed")}">
                        ${taskEntity.completed?string("Завершена", "Не завершена")}
                    </span>
                </td>
                <td class="action-buttons">
                    <a href="/tasks/edit/${taskEntity.id?c}" class="btn btn-primary">Редактировать</a>
                    <form action="/tasks/${taskEntity.id?c}" method="post" style="display:inline;">
                        <input type="hidden" name="_method" value="delete"/>
                        <button type="submit" class="btn btn-danger">Удалить</button>
                    </form>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>

    <!-- Пагинация -->
    <@paginationFragments.pagination
    currentPage=page.number!0
    totalPages=page.totalPages!1
    pageSize=page.size!10
    baseUrl="/tasks"
    queryParams="&name=${taskFilter.name!}&completed=${taskFilter.completed!}"
    />
</@base.layout>
