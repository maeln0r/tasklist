<#macro pagination currentPage pageSize totalPages baseUrl queryParams>
    <div class="pagination">
        <!-- Кнопка для перехода на первую страницу -->
        <#if (currentPage > 0)>
            <a href="${baseUrl}?pageNumber=0&pageSize=${pageSize}${queryParams}" class="btn btn-secondary">&laquo;</a>
        </#if>

        <!-- Кнопка для перехода на предыдущую страницу -->
        <#if (currentPage > 0)>
            <a href="${baseUrl}?pageNumber=${currentPage - 1}&pageSize=${pageSize}${queryParams}"
               class="btn btn-secondary">&lsaquo;</a>
        </#if>

        <!-- Отображение номеров страниц -->
        <#list 0..totalPages-1 as page>
            <#if (page >= currentPage - 2 && page <= currentPage + 2)>
                <#if (page == currentPage)>
                    <span class="btn btn-primary">${page + 1}</span>
                <#else>
                    <a href="${baseUrl}?pageNumber=${page}&pageSize=${pageSize}${queryParams}"
                       class="btn btn-secondary">${page + 1}</a>
                </#if>
            </#if>
        </#list>

        <!-- Кнопка для перехода на следующую страницу -->
        <#if (currentPage < totalPages - 1)>
            <a href="${baseUrl}?pageNumber=${currentPage + 1}&pageSize=${pageSize}${queryParams}"
               class="btn btn-secondary">&rsaquo;</a>
        </#if>

        <!-- Кнопка для перехода на последнюю страницу -->
        <#if (currentPage < totalPages - 1)>
            <a href="${baseUrl}?pageNumber=${totalPages - 1}&pageSize=${pageSize}${queryParams}"
               class="btn btn-secondary">&raquo;</a>
        </#if>
    </div>
</#macro>
