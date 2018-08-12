package oper_project.domain;

import oper_project.methods.HashString;

/**
 * Created by Олег on 08.06.2018.
 */
public class Operator {
    private int ID;
    private String Login;
    private String Password;
    private String Role;

    public Operator() {
    }

    public Operator(int ID, String login, String password, String role) {
        this.ID = ID;
        Login = login;
        Password = new HashString(password, "MD5").getHash();
        Role = role;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = new HashString(password, "MD5").getHash();
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "ID=" + ID +
                ", Login='" + Login + '\'' +
                ", Role='" + Role + '\'' +
                '}';
    }
}
