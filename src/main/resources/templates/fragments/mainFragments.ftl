<#macro header pageTitle>
    <header class="main-header container">
        <h1>Список дел</h1>
        <nav class="main-nav">
            <a href="/tasks" class="nav-link">Дела</a>
            <a href="/tasks/add" class="nav-link">Добавить</a>
        </nav>
        <hr>
        <#if username??>
            <span>Привет, ${username}!</span>
            <a href="/logout" class="nav-link">Выйти</a>
        </#if>
    </header>
</#macro>

<#macro footer>
    <footer class="main-footer container">
        <hr>
        <p>&copy; ${.now?string("yyyy")} Список дел</p>
    </footer>
</#macro>
