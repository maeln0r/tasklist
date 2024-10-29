<#macro filterForm action taskFilterDto>
    <form action="${action}" method="get" class="filter-form">
        <input type="hidden" name="pageNumber" value="${taskFilterDto.pageSize!0}">
        <div class="form-group">
            <label for="name">Имя задачи:</label>
            <input type="text" id="name" name="name" value="${taskFilterDto.name!}" class="form-control"/>
        </div>
        <div class="form-group">
            <label for="completed">Статус:</label>
            <select id="completed" name="completed" class="form-control">
                <option value="">Все</option>
                <option value="yes" <#if taskFilterDto.completed?? && taskFilterDto.completed?string == "yes">selected</#if>>
                    Завершены
                </option>
                <option value="no" <#if taskFilterDto.completed?? && taskFilterDto.completed?string == "no">selected</#if>>Не
                    завершены
                </option>
            </select>
        </div>
        <div class="form-group">
            <label for="pageSize">Размер страницы:</label>
            <select id="pageSize" name="pageSize" class="form-control">
                <option value="5" <#if taskFilterDto.pageSize?? && taskFilterDto.pageSize == 5>selected</#if>>5</option>
                <option value="10" <#if !taskFilterDto.pageSize?? || taskFilterDto.pageSize == 10>selected</#if>>10</option>
                <option value="20" <#if taskFilterDto.pageSize?? && taskFilterDto.pageSize == 20>selected</#if>>20</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Применить фильтр</button>
        <#if filterError??>
            <div class="alert alert-error">
                <#list filterError as error>
                    <p>${error}</p>
                </#list>
            </div>
        </#if>
    </form>
</#macro>
