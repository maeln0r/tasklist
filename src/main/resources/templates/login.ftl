<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="/auth-static/styles.css">
    <link rel="icon" href="/auth-static/favicon.ico" type="image/x-icon">
</head>
<body>
<div class="login-container">
    <div class="login-box">
        <h2>Форма входа</h2>
        <form action="/perform_login" method="post">
            <div class="input-group">
                <label for="username">Логин:</label>
                <input type="text" id="username" name="username" placeholder="Enter your username" required>
            </div>

            <div class="input-group">
                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
            </div>

            <button type="submit" class="btn">Войти</button>
        </form>

        <#if error?? && error>
            <p class="error-message">Ошибка авторизации</p>
        </#if>
    </div>
</div>
</body>
</html>
