<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Login Page</title>
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap-4.1.1-dist/js/bootstrap.js"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap-4.1.1-dist/css/bootstrap.min.css" /> " />
</head>

<body>

<div class="container" style="width: 450px;">
    <h3>You need to authorize</h3>
    <c:url value="/j_spring_security_check" var="loginUrl" />
    <form action="${loginUrl}" method="post">
        <div class="form-group">
            <label for="inputLogin">Login</label>
            <input type="text" name="j_username" class="form-control" id="inputLogin" placeholder="Login" required autofocus>
        </div>
        <div class="form-group">
            <label for="inputPassword">Password</label>
            <input type="password" name="j_password" class="form-control" id="inputPassword" placeholder="Password" required>
        </div>
        <button type="submit" class="btn btn-primary">Sign in</button>
    </form>
    <h3>Need an account? <a href="/reg">Sign up</a></h3><br/>
    <a href="/index/">Main page</a>
</div>



</body>
</html>