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
    <title>Register Page</title>
    <script type="text/javascript" src="/resources/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap-4.1.1-dist/js/bootstrap.js"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap-4.1.1-dist/css/bootstrap.min.css" /> " />
    <script type="text/javascript" src="/resources/js/password_check.js"></script>

</head>

<body>
<div class="container" style="width: 450px;">
    <h3>Register a new account</h3>
    <form action="/reg" method="post">
        <div class="form-group">
            <label for="login">Login</label>
            <input type="text" name="login" class="form-control" id="login" placeholder="Login" required autofocus>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" name="password" class="form-control" id="password" placeholder="Password" required>
        </div>
        <div class="form-group">
            <label for="password2">Confirm password</label>
            <input type="password" name="password2" class="form-control" id="password2" placeholder="Password" required>
        </div>
        <button type="submit" class="btn btn-primary">Sing up</button>
    </form>
    <h3>Already registered? <a href="/login">Sign in</a></h3><br/>
    <a href="/index/">Main page</a>
</div>

</body>
</html>