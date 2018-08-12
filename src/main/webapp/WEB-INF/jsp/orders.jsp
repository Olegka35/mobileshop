<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 17.06.2018
  Time: 14:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<html>
<head>
    <title>Order list</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap-4.1.1-dist/css/bootstrap.min.css" /> " />
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mystyle.css" /> " />
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="/resources/js/orders.js"></script>
    <link rel="stylesheet" href="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.10.3/themes/sunny/jquery-ui.css">
    <script type="text/javascript" src="/resources/js/jquery.textchange.js"></script>
    <script type="text/javascript" src="/resources/js/moment.js"></script>
    <script type="text/javascript" src="/resources/bootstrap-4.1.1-dist/js/bootstrap.js"></script>
    <link href="/resources/msgGrowl/css/msgGrowl.css" rel="stylesheet" />
    <script src="/resources/msgGrowl/msgGrowl.min.js"></script>
</head>
<body>
<div class = "container-fluid">
    <nav class="nav nav-inline">
        <a class="nav-link active" href="/basket"><security:authentication property="principal.username" /></a>
        <a class="nav-link" href="/index">Main page</a>
        <a class="nav-link" href="/params">Params page</a>
        <a class="nav-link" href="<c:url value="/logout"/>">Logout</a>
    </nav>
    <div class = "row">
        <div class = "col-md-6">
            <table id="orders" class="lux">
                <thead style="background: #00FFFF;"><tr>
                    <th onclick="sort('object_id');">ORDER_ID</th>
                    <th onclick="sort('Order_Price');">Price</th>
                    <th onclick="sort('Order_Date');">Date</th>
                    <th onclick="sort('Order_Status');">Status</th>
                    <th></th>
                </tr></thead>
                <tbody id="table_body">
                </tbody>
            </table>
            <br/>
            <button id="PrevPage"><<< BACK</button>
            <div id="PageCounter">PAGE ... OF ...</div>
            <button id="NextPage">NEXT >>></button>
        </div>

        <div class = "col-md-5">
            <input type="text" id="search" placeholder="Enter article name"/>
            <button onclick="searchArticle();">Search</button>
            <br/><br/>
            <input type="checkbox" id="showWaiting" checked>Show waiting<br/>
            <input type="checkbox" id="showProcessing" checked>Show processing<br/>
            <input type="checkbox" id="showCompleted" checked>Show completed<br/>
        </div>
    </div>
</div>

<div id="order_info" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Order information</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div id="window_body" class="modal-body">

            </div>
        </div>
    </div>
</div>

<input id="pageNum" type="hidden" value="1"/>
<input id="page" type="hidden" value="1"/>
<input id="sort" type="hidden" value="object_id"/>
<input id="reversed" type="hidden" value="1"/>

</body>
</html>
