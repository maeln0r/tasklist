<#-- Импорт основного шаблона -->
<#import "/base.ftl" as base />

<@base.layout>
    <h2>Профиль пользователя</h2>

    <!-- Информация о пользователе -->
    <div class="profile-container">
        <p><strong>Имя пользователя:</strong> ${user.username}</p>
        <p><strong>Email:</strong> ${user.email}</p>
    </div>

    <!-- Форма для изменения пароля -->
    <div class="profile-form-container">
        <h3>Изменить локальный пароль</h3>
        <form action="/user/change-password" method="post">
            <input type="hidden" name="user_id" value="${user.id}">
            <p>
                <label for="password">Новый пароль:</label>
                <input type="password" id="password" name="password" required>
            </p>
            <p class="tar">
                <button type="submit" class="btn btn-primary">Сохранить</button>
            </p>
        </form>
        <#-- Проверяем, доступен ли объект request и был ли успешно изменен пароль -->
        <#if request?? && request.getParameter("passwordChanged")??>
            <div class="alert alert-success">
                Пароль успешно изменен
            </div>
        </#if>
    </div>
</@base.layout>
