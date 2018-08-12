<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 09.06.2018
  Time: 9:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Management</title>
</head>
<body>
<table>
    <tbody>
    <tr>
        <td style="width: 50%; position: relative; left: auto; top: 0; vertical-align: top">
            <h3>Заказы</h3>
            <ul>
                {{#each reports}}
                <li id='report{{this.ID}}' style="cursor: pointer">
                    {{this.report_name}} ({{ formatDate this.date }}) {{ getAssign this.agent }} | {{getStatusName this.status}}
                    {{#if this.agent_surname}}
                    <{{this.agent_surname}}>
                    {{/if}}
                </li>
                {{/each}}
            </ul>
        </td>
        <td style="width: 50%; position: absolute; left:50%; right: 0; top: auto; vertical-align: top">
            <div id="description" style="padding:10px; border: 3px solid black;">Выберите проблему из списка</div>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
