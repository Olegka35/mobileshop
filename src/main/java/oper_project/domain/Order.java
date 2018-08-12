package oper_project.domain;

import java.util.Date;
import java.util.List;

public class Order {
    private Integer id;
    private Operator user;
    private String name;
    private String email;
    private String address;
    private String number;
    private Date date;
    private Integer price;
    private Integer status;
    private String payType;
    private List<OrderItem> orderItems;

    public Order(Integer id, Operator user, String name, String email, String address, String number, Date date, Integer price, Integer status, String payType, List<OrderItem> orderItems) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.email = email;
        this.address = address;
        this.number = number;
        this.date = date;
        this.price = price;
        this.status = status;
        this.payType = payType;
        this.orderItems = orderItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Operator getUser() {
        return user;
    }

    public void setUser(Operator user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
