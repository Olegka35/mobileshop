/**
 * Created by Олег on 11.05.2017.
 */
window.onload = function () {
    document.getElementById("password").onchange = checkPasswords;
    document.getElementById("password2").onchange = checkPasswords;
}

function checkPasswords()
{
    var passl = document.getElementById("password");
    var pass2 = document.getElementById("password2");
    if(passl.value != pass2.value)
        passl.setCustomValidity("Passwords are not equal!");
    else
        passl.setCustomValidity("");
}