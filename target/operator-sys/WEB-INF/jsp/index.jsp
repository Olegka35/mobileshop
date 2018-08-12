<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 09.06.2018
  Time: 16:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<html>
<head>
    <title>Mobile phones shop</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap-4.1.1-dist/css/bootstrap.min.css" /> " />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mystyle.css" /> " />
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="/resources/js/index.js"></script>
    <link rel="stylesheet" href="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.10.3/themes/sunny/jquery-ui.css">
    <script type="text/javascript" src="/resources/js/jquery.textchange.js"></script>
    <script type="text/javascript" src="/resources/js/moment.js"></script>
    <script type="text/javascript" src="/resources/bootstrap-4.1.1-dist/js/bootstrap.min.js"></script>
    <link href="/resources/msgGrowl/css/msgGrowl.css" rel="stylesheet" />
    <script src="/resources/msgGrowl/msgGrowl.min.js"></script>
</head>
<body>

<div class = "container-fluid">
    <div class = "row">
        <div class = "col-md-6">
            <nav class="nav nav-inline">
                <security:authorize access="isAuthenticated()">
                    <b><a class="nav-link active" href="<c:url value="/basket"/>"><security:authentication property="principal.username" /></a></b>
                    <security:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                        <a class="nav-link" href="<c:url value="/params"/>">Moderator page</a>
                        <a class="nav-link" href="<c:url value="/orders"/>">Order list</a>
                    </security:authorize>
                    <a class="nav-link" href="<c:url value="/logout"/>">Logout</a>
                    <security:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                        <b><a class="nav-link btn btn-primary" href="#popup_add" data-toggle="modal">ADD ARTICLE</a></b>
                    </security:authorize>
                </security:authorize>
                <security:authorize access="!isAuthenticated()">
                    <a class="nav-link" href="<c:url value="/login"/>">Login</a>
                    <a class="nav-link" href="<c:url value="/reg"/>">Register</a>
                </security:authorize>
            </nav>
        </div>
    </div>
</div>

<div class = "container-fluid">
<div class = "row">
<div class = "col-md-7">
    <table id="results" class="lux">
        <thead><tr>
            <th onclick="sort('object_id');" width="20px">Article</th>
            <th onclick="sort('Type');" width="125px">Name</th>
            <th onclick="sort('Price');" width="30px">Price</th>
            <th onclick="sort('Num');" width="20px">On stock</th>
        </tr></thead>
        <tbody id="table_body" class="stripy">
        <c:forEach items="${articleList.articleList}" var="article">
            <tr id="${article.id}" class="article_row">
                <td>${article.id}</td>
                <td class="wordwrap">${article.type}</td>
                <td class="wordwrap">${article.price}$</td>
                <td class="wordwrap">${article.num}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <br/>
    <button id="PrevPage"><<< BACK</button>
    <div id="PageCounter">PAGE ${articleList.page} OF ${articleList.pageNum}</div>
    <button id="NextPage">NEXT >>></button>
</div>
<div class = "col-md-5">
    <td style="vertical-align:top;padding:10px;">
        <input type="text" id="search"/>
        <button onclick="searchArticle(); return false;">Search</button>
        <br/><br/>
        <input type="checkbox" id="hideEmpty">Hide out of stock<br/>
        <br/>
        <div class="card bg-light mb-3" style="max-width: 18rem;">
            <div id="description" class="card-body">
                Select an article from the list.
            </div>
        </div>
    </td>
</div>
</div></div>

<div id="popup_edit" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Update the article</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <form id="edit_form_post" method="post" onsubmit="javascript: editArticle(); return false;">
                    <table id="edititems">
                        <tr>
                            <td>ID:</td>
                            <td><input type="number" name="ID" id="edit_ID" readonly /></td>
                        </tr>
                        <tr>
                            <td>Type:</td>
                            <td><input type="text" name="Type" id="edit_Type" maxlength="30" required /></td>
                        </tr>
                        <tr>
                            <td>Price:</td>
                            <td><input type="number" min="0" name="Price" id="edit_Price" maxlength="10" required /></td>
                        </tr>
                        <tr>
                            <td>On stock:</td>
                            <td><input type="number" min="0" name="Num" id="edit_Num" maxlength="10" required /></td>
                        </tr>
                        <c:forEach items="${articleList.params}" var="item">
                            <tr class="rowparams">
                                <td>${item['attribute_name']}:</td>
                                <td><input type="text" name="${item['attribute_name']}" id="edit_${item['attribute_name']}" maxlength="20" required/></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <input id="submitEdit" type="submit" value="UPDATE" />
                </form>
            </div>
        </div>
    </div>
</div>

<div id="popup_add" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Add the article</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <form id="add_form_post" method="post" onsubmit="javascript: addArticle(); return false;">
                    <table id="additems">
                        <tr>
                            <td>Type:</td>
                            <td><input type="text" name="Type" id="add_type" maxlength="30" required /></td>
                        </tr>
                        <tr>
                            <td>Price:</td>
                            <td><input type="number" min="0" name="Price" id="add_price" maxlength="10" required /></td>
                        </tr>
                        <tr>
                            <td>On stock:</td>
                            <td><input type="number" min="0" name="Num" id="add_num" maxlength="10" required /></td>
                        </tr>
                        <c:forEach items="${articleList.params}" var="item">
                            <tr class="rowparams">
                                <td>${item['attribute_name']}:</td>
                                <td><input type="text" name="${item['attribute_name']}" id="add_${item['attribute_name']}" maxlength="20" required/></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <input id="submitAdd" type="submit" value="ADD" />
                </form>
            </div>
        </div>
    </div>
</div>

<security:authorize access="hasRole('ROLE_ADMIN')"><input id="isAdmin" type="hidden" value="true"/></security:authorize>
<security:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')"><input id="isManager" type="hidden" value="true"/></security:authorize>

<input id="pageNum" type="hidden" value="${articleList.pageNum}"/>
<input id="page" type="hidden" value="${articleList.page}"/>
<input id="sort" type="hidden" value="object_id"/>
<input id="reversed" type="hidden" value="0"/>
</body>
</html>
