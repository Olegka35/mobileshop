<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 09.06.2018
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<html>
<head>
    <title>Article params management</title>
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap-4.1.1-dist/js/bootstrap.min.js"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap-4.1.1-dist/css/bootstrap.min.css" /> " />
    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.textchange.js"></script>
    <script type="text/javascript" src="/resources/js/params.js"></script>
    <link href="/resources/msgGrowl/css/msgGrowl.css" rel="stylesheet" />
    <script src="/resources/msgGrowl/msgGrowl.min.js"></script>
</head>
<body>
<div class = "container-fluid">
    <div class = "row">
        <div class = "col-md-6">
            <nav class="nav nav-inline">
                <security:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                    <b><a class="nav-link active" href="<c:url value="/basket"/>"><security:authentication property="principal.username" /></a></b>
                    <a class="nav-link" href="<c:url value="/index"/>">Main page</a>
                    <a class="nav-link" href="<c:url value="/orders"/>">Order list</a>
                    <a class="nav-link" href="<c:url value="/logout"/>">Logout</a>
                </security:authorize>
            </nav>
        </div>
    </div>
</div>
<table>
    <tbody>
    <tr>
        <td style="width: 40%; position: relative; left: auto; top: 0; vertical-align: top">
            <h3>Parameter list</h3>
            <ul>
                <c:forEach items="${params}" var="item">
                <p id="attr_${item['attribute_id']}" style="font-size: 16px">${item['attribute_name']} | <a id="${item['attribute_id']}" class="delete_attr"><span class="icon" style="cursor: pointer">✖</span></a></p>
                </c:forEach>
            </ul>
        </td>
        <td style="width: 30%; position: relative; left: 40px; top: 20px; vertical-align: top">
            <input id="newparam" maxlength="16" placeholder="Enter the new parameter" />
            <button onclick="add_param();">ADD</button>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
