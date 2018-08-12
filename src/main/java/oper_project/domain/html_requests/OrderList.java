package oper_project.domain.html_requests;

import oper_project.configuration.PageConfiguration;
import oper_project.domain.Order;

import java.util.List;

public class OrderList {
    private List<Order> orderList;
    private Integer page;
    private Integer pageNum;
    private String error = null;

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageNumEx(Integer num)
    {
        setPageNum(num / PageConfiguration.ITEMS_ON_PAGE + (num % PageConfiguration.ITEMS_ON_PAGE > 0 ? 1 : 0));
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
