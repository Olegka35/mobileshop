<%--
  Created by IntelliJ IDEA.
  User: Олег
  Date: 15.06.2018
  Time: 11:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<html>
<head>
    <title>Basket</title>
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
            <a class="nav-link active" href="#"><security:authentication property="principal.username" /></a>
            <a class="nav-link" href="/index">Back to shop</a>
            <security:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                <a class="nav-link" href="<c:url value="/params"/>">Moderator page</a>
                <a class="nav-link" href="<c:url value="/orders"/>">Order list</a>
            </security:authorize>
            <a class="nav-link" href="<c:url value="/logout"/>">Logout</a>
        </nav>

        <table id="basket_table" class="table">
            <thead class="thead-default"><tr>
                <th>Article Name</th>
                <th>Price</th>
                <th>Num</th>
            </tr></thead>
            <tbody id="table_body">
            <c:forEach items="${basket}" var="basket_item">
                <tr id="bi_row_${basket_item.id}" class="bi_row">
                    <td class="wordwrap">${basket_item.article.type}</td>
                    <td class="wordwrap">${basket_item.article.price}$</td>
                    <td class="wordwrap">${basket_item.num}</td>
                    <td class="wordwrap"><button id="delete_basket_${basket_item.id}" type="button" class="btn btn-primary delbasket">Delete</button></td>
                </tr>
            </c:forEach>
            <tr class="table-info"><td colspan="4"><b>Overall price</b>: $<a id="ov_price"></a></td></tr>
            </tbody>
        </table>
        <a class="btn btn-success" style="float: right" onclick="javascript: checkBasket();">Make an order</a>
        <%--<br/><br/><br/>--%>
        </div>
        <div class = "col-md-6">
            <h2>Last orders:</h2>
        <table id="my_orders" class="table">
            <thead class="thead-default"><tr>
                <th>ORDER_ID</th>
                <th>Price</th>
                <th>Date</th>
                <th>Status</th>
                <th></th>
            </tr></thead>
            <tbody id="my_orders_body">
            </tbody>
        </table>
        </div>
    </div>
</div>

<div id="make_order_popup" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Make an order</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <form id="make_order_form" method="post" onsubmit="javascript: make_order(); return false;">
                    <div class="form-group">
                        <label for="input_name">Full Name</label>
                        <input type="text" class="form-control" name="name" id="input_name" placeholder="Enter your name" required>
                    </div>
                    <div class="form-group">
                        <label for="input_email">Email address</label>
                        <input type="email" class="form-control" name="email" id="input_email" aria-describedby="emailHelp" placeholder="Enter your email" required>
                        <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
                    </div>
                    <div class="form-group">
                        <label for="input_telephone">Telephone number</label>
                        <input type="tel" class="form-control" name="telephone" id="input_telephone" placeholder="Enter your telephone number" required>
                    </div>
                    <div class="form-group">
                        <label for="input_address">Address</label>
                        <input type="text" class="form-control" name="address" id="input_address" placeholder="Enter your address" required>
                    </div>
                    <fieldset class="form-group">
                        <legend class="col-form-label">Choose pay type</legend>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="pay_type" id="gridRadios1" value="cash" checked>
                            <label class="form-check-label" for="gridRadios1">
                                By cash
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="pay_type" id="gridRadios2" value="bankcard">
                            <label class="form-check-label" for="gridRadios2">
                                By bank card
                            </label>
                        </div>
                    </fieldset>
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script language="JavaScript" type="text/javascript">
    getOverallPrice();
    showMyOrders('<security:authentication property="principal.username" />');
</script>
</body>
</html>
