<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 05.03.2017
  Time: 23:19
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mystyle.css" /> " />
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="/resources/js/todolist.js"></script>
    <link rel="stylesheet" href="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.10.3/themes/sunny/jquery-ui.css">
    <script type="text/javascript" src="/resources/js/jquery.textchange.js"></script>
    <script type="text/javascript" src="/resources/js/moment.js"></script>

    <title>ToDoList Page</title>
</head>

<body>
<security:authorize access="isAuthenticated()">
    <a href="<c:url value="/logout"/>">Logout</a>
</security:authorize>
<security:authorize access="hasRole('ROLE_ADMIN')">
    | <a href="<c:url value="/admin/"/>">Admin Page</a>
</security:authorize>
<p class="header">${articleList.login}'s Task List</p>
<button class="addbutton" name="addtask" onclick="showInsert(); return false;">ADD TASK</button>

<table>
    <tr>
        <td>
            <div class="tablediv">
            <table id="results" class="lux">
                <thead><tr>
                    <th onclick="sort('object_id');" width="30px">ID</th>
                    <th onclick="sort('name');" width="125px">Name</th>
                    <th class="descr">Description</th>
                    <th onclick="sort('priority');" width="75px">Priority</th>
                    <th onclick="sort('status');" width="90px">Status</th>
                    <th onclick="sort('due_date');"width="100px">Due Date</th>
                    <th width="25px"></th>
                    <security:authorize access="hasRole('ROLE_ADMIN')">
                        <th width="25px"></th>
                    </security:authorize>
                </tr></thead>
                <tbody id="table_body" class="stripy">
                <c:forEach items="${articleList.articleList}" var="article">
                    <tr id="${article.id}" class="tabrow">
                        <td>${article.id}</td>
                        <td id="todo-${article.id}-name" class="wordwrap">${article.name}</td>
                        <td id="todo-${article.id}-desc" class="wordwrap"><div class="heightlimit">${article.description}</div></td>
                        <td id="todo-${article.id}-prior"><script language="JavaScript" type="text/javascript">document.getElementById("oper_project-${article.id}-prior").innerHTML = getPriority(${article.priority});</script></td>
                        <td id="todo-${article.id}-status"><script language="JavaScript" type="text/javascript">document.getElementById("oper_project-${article.id}-status").innerHTML = getStatus(${article.status});</script></td>
                        <td id="todo-${article.id}-duedate"><script language="JavaScript" type="text/javascript">document.getElementById("oper_project-${article.id}-duedate").innerHTML = formatDate('${article.dueDate}');</script></td>
                        <td><a class="show_popup" rel="edit" onclick="showEdit(${article.id}, ${article.priority}, ${article.status}, '${articleList.login}'); return false;"><span class="icon">✍</span></a></td>
                        <security:authorize access="hasRole('ROLE_ADMIN')">
                            <td><a href="delete/${article.id}"><span class="icon">✖</span></a></td>
                        </security:authorize>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
            <br/>
            <button id="PrevPage"><<< BACK</button>
            <div id="PageCounter">PAGE ${articleList.page} OF ${articleList.pageNum}</div>
            <button id="NextPage">NEXT >>></button>
        </td>
        <td style="vertical-align:top;padding:10px;">
            <label for="filterStatus">Status:</label>
            <select name="filterStatus" id="filterStatus" size="1" required>
                <option value="0">All</option>
                <option value="1">Waiting</option>
                <option value="2">In progress</option>
                <option value="3">Completed</option>
            </select>
            <button onclick="filterTable('status');return false;">Filter</button>
            <br/>
            <label for="filterPriority">Priority:</label>
            <select name="filterPriority" id="filterPriority" size="1" required>
                <option value="0">All</option>
                <option value="1">Low</option>
                <option value="2">Middle</option>
                <option value="3">High</option>
                <option value="4">Emergency</option>
            </select>
            <button onclick="filterTable('priority');return false;">Filter</button>
            <br/><br/>
            <input type="text" id="search"/>
            <button onclick="searchArticle(); return false;">Search</button>
        </td>

        <script language="JavaScript" type="text/javascript">
            $('#filterStatus').val(${articleList.status});
            $('#filterPriority').val(${articleList.priority});
            $('#search').val('${articleList.search}');
        </script>
    </tr>
</table>

<div class="popup edit">
    <a class="close" href="#">✖</a>
    <h2>Update the task</h2>
    <form method="post" action="edit/">
        <p>ID:
        <input type="text" name="ID" id="ID" readonly />
        </p>
        <p>User:
        <input type="text" name="user" id="user" required/>
        <div id="block-search-result"><ul id="list-search-result"></ul></div>
        </p>
        <p>Name:
        <input type="text" name="name" id="name" maxlength="40" required/>
        </p>
        <p>Description:
        <textarea rows="10" cols="45" name="descr" id="descr" maxlength="1000" required></textarea>
        </p>
        <p>Priority:
        <select name="priority" id="priority" size="1" required>
            <option value="1">Low</option>
            <option value="2">Middle</option>
            <option value="3">High</option>
            <option value="4">Emergency</option>
        </select>
        </p>
        <p>Status:
        <select name="status" id="status" size="1" required>
            <option value="1">Waiting</option>
            <option value="2">In progress</option>
            <option value="3">Completed</option>
        </select>
        </p>
        <p>Due Date: <input id="duedate" name="duedate" type="text" class="datepicker" required></p>
        <input id="submitEdit" type="submit" value="UPDATE" />
    </form>
</div>

<div class="popup add">
    <a class="close" href="#">✖</a>
    <h2>Add the task</h2>
    <form method="post" action="add/">
        <p>Name:
        <input type="text" name="addname" id="addname" maxlength="40" required/>
        </p>
        <p>Description:
        <textarea rows="10" cols="45" name="adddescr" id="adddescr" maxlength="1000" required></textarea>
        </p>
        <p>Priority:
        <select name="addpriority" id="addpriority" size="1" required>
            <option value="1">Low</option>
            <option value="2">Middle</option>
            <option value="3">High</option>
            <option value="4">Emergency</option>
        </select>
        </p>
        <p>Status:
        <select name="addstatus" id="addstatus" size="1" required>
            <option value="1">Waiting</option>
            <option value="2">In progress</option>
            <option value="3">Completed</option>
        </select>
        </p>
        <p>Due Date: <input id="duedate_add" name="duedate_add" type="text" class="datepicker" required></p>
        <input id="submitAdd" type="submit" value="ADD" />
    </form>
</div>

<security:authorize access="hasRole('ROLE_ADMIN')"><input id="isAdmin" type="text" hidden="true" value="true"/></security:authorize>
<security:authorize access="!hasRole('ROLE_ADMIN')"><input id="isAdmin" type="text" hidden="true" value="false"/></security:authorize>

<input id="pageNum" type="text" hidden="true" value="${articleList.pageNum}"/>
<input id="page" type="text" hidden="true" value="${articleList.page}"/>
<input id="user_login" type="text" hidden="true" value="${articleList.login}"/>

</body>
</html>