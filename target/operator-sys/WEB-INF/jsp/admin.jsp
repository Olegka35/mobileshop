<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 07.04.2017
  Time: 22:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<html>
<head>
    <title>Admin Page</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mystyle.css" /> " />
    <script type="text/javascript" src="/resources/js/jquery.min.js"></script>
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.textchange.js"></script>
</head>
<body>
<security:authorize access="isAuthenticated()">
    <a href="<c:url value="/logout"/>">Logout</a>
</security:authorize>
<security:authorize access="hasRole('ROLE_ADMIN')">
    | <a href="<c:url value="/todolist/"/>">TodoList Page</a>
</security:authorize>
<h2>Operator Page</h2>
<table>
    <tbody>
    <tr>
        <td style="vertical-align: top">
            <div style="max-width: 475px">
            <table id="results" class="lux">
                <tbody>
                <h2 style="text-align: center">User List</h2>
                <th width="25px">ID</th>
                <th width="175px">Login</th>
                <th width="100px">Change Role</th>
                <th width="100px">Tasks</th>
                <th width="60px"></th>
                <c:forEach items="${userList.userList}" var="user">
                    <tr class="userrow">
                        <td>${user.ID}</td>
                        <td>${user.login}</td>
                        <c:if test="${user.role eq 'ROLE_USER'}">
                            <td><button onclick="changeRole(${user.ID}); return false;">Make Admin</button></td>
                        </c:if>
                        <c:if test="${user.role eq 'ROLE_ADMIN'}">
                            <td><button onclick="changeRole(${user.ID}); return false;">Make User</button></td>
                        </c:if>
                        <td><a href="../todolist/${user.ID}">View Tasks</a></td>
                        <td><button onclick="deleteUser(${user.ID}); return false;">Delete</button></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
            <br/>
            <button id="PrevPage"><<< BACK</button>
            <div id="PageUserCounter">PAGE ${userList.page} OF ${userList.pageNum}</div>
            <button id="NextPage">NEXT >>></button>
        </td>
        <td width="100px"></td>
        <div style="max-width: 300px">
        <td style="vertical-align: top">
            <h2 style="text-align: center;">Task Search</h2>
            <input type="text" id="search"/>
            <div id="block-search-result"><table id="list-search-result" class="lux wordwrap"><th>Name</th><th>Description</th><th width="80px">User page</th></table></div>
        </td>
        </div>
    </tr>
    </tbody>
</table>

<input id="userPageNum" type="text" hidden="true" value="${userList.pageNum}"/>
<input id="userPage" type="text" hidden="true" value="${userList.page}"/>

</body>
</html>
