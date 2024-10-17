<#import "/fragments/mainFragments.ftl" as mainFragments />

<#macro layout>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>${pageTitle!''}</title>
        <link rel="stylesheet" href="/css/styles.css">
    </head>
    <body>
    <@mainFragments.header pageTitle=pageTitle!''/>
    <main class="taskEntity-list-container container">
        <#nested>
    </main>
    <@mainFragments.footer/>
    </body>
    </html>
</#macro>
