package oper_project.service;

import oper_project.domain.Order;
import oper_project.domain.OrderItem;

import java.util.List;

public interface OrderService {
    void addOrder(Order order);
    void updateStatus(Integer order_id, Integer newStatus);
    Order getById(Integer order_id);
    List<Order> getOrderList();
    List<Order> getOrderList(Integer page);
    List<Order> getOrderList(Integer page, String order_field, Boolean reversed, String filter, String namePart);
    List<Order> getUserOrderList(Integer userId);
    List<Order> getLastUserOrderList(Integer userId, Integer num);
    Integer getOrdersNumber();
    Integer getOrdersNumber(String filter, String namePart);
    List<OrderItem> getByArticleNamePart(String namePart);
    List<Integer> getOrderIDsByArticleNamePart(String namePart);
}
