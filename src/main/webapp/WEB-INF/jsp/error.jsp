<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<html>
<head>
    <title>Access denied</title>
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap-4.1.1-dist/js/bootstrap.js"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap-4.1.1-dist/css/bootstrap.min.css" /> " />
</head>
<body>
    <div class = "container-fluid">
        <div class = "row">
            <div class = "col-md-5">
                <div class="alert alert-danger" role="alert">
                    The request resource is not available for you!<br/>
                    <form action="/login">
                        <button type="submit" class="btn btn-primary">Login page</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
