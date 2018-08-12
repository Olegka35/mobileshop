package oper_project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import oper_project.domain.*;
import oper_project.domain.html_requests.OrderList;
import oper_project.service.ArticleService;
import oper_project.service.BasketService;
import oper_project.service.OperatorService;
import oper_project.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class OrderController {
    private final ArticleService articleService;
    private final OperatorService operatorService;
    private final BasketService basketService;
    private final OrderService orderService;

    @Autowired
    public OrderController(ArticleService articleService, OperatorService operatorService, BasketService basketService, OrderService orderService) {
        this.articleService = articleService;
        this.operatorService = operatorService;
        this.basketService = basketService;
        this.orderService = orderService;
    }

    @RequestMapping(value = "/basket", method = RequestMethod.GET)
    public ModelAndView showBasket(HttpServletRequest request, HttpServletResponse response) {
        List<BasketItem> basket = new ArrayList<BasketItem>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = operatorService.getUser(auth.getName()).getID();
        basket = basketService.getByUserID(userID);
        return new ModelAndView("basket", "basket", basket);
    }

    @RequestMapping(value = "/basket_price", method = RequestMethod.POST)
    @ResponseBody
    public Integer getOrderInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return basketService.getPrice(operatorService.getUser(auth.getName()).getID());
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView showOrderList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("orders");
    }

    @RequestMapping(value = "/order_list", method = RequestMethod.POST)
    @ResponseBody
    public OrderList showOrderList(HttpServletRequest request, @RequestParam("page") Integer page, @RequestParam("sort_field") String sort, @RequestParam("reversed") Boolean reversed,
                                   @RequestParam("filters") String filters, @RequestParam("namePart") String part) {
        if(part != null && part.length() == 0) part = null;
        OrderList orderList = new OrderList();
        if(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")) {

            List<Order> listOfOrders = orderService.getOrderList(page, sort, reversed, filters, part);
            orderList.setOrderList(listOfOrders);
            orderList.setPage(page);
            orderList.setPageNumEx(orderService.getOrdersNumber(filters, part));
        }
        return orderList;
    }

    @RequestMapping(value = "/my_orders", method = RequestMethod.POST)
    @ResponseBody
    public List<Order> showOrderList(@RequestParam("login") String login) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        List<Order> orderList = new ArrayList<>();
        if(user != null && user.getLogin().equals(login)) {
            orderList = orderService.getLastUserOrderList(user.getID(), 5);
        }
        return orderList;
    }

    @RequestMapping(value = "/order_info", method = RequestMethod.POST)
    @ResponseBody
    public Order getOrderInfo(HttpServletRequest request, @RequestParam("id") Integer id) {
        if(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")) {
            return orderService.getById(id);
        }
        return null;
    }

    @RequestMapping(value = "/order_status", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getOrderInfo(HttpServletRequest request, @RequestParam("id") Integer id, @RequestParam("status") Integer status) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        if(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")) {
            orderService.updateStatus(id, status);
            responseMap.put("error", false);
        }
        else {
            responseMap.put("error", true);
            responseMap.put("text", "Access denied");
        }
        return responseMap;
    }

    @RequestMapping(value = "/get_types", method = RequestMethod.POST)
    @ResponseBody
    public Set<String> getOrderArticleTypes(HttpServletRequest request, @RequestParam("namePart") String namePart) {
        if(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")) {
            List<OrderItem> orderItems = orderService.getByArticleNamePart(namePart);
            Set<String> types = new HashSet<>();
            for(OrderItem item: orderItems)
                types.add(item.getArticleTypeName());
            return types;
        }
        return null;
    }

    @RequestMapping(value = "/add_to_basket", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addToBasket(@RequestParam("article_id") Integer article_id, @RequestParam("num") Integer num) {
        Map<String, Object> responseMap = new HashMap<String, Object>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();
        if(login.equals("anonymousUser")) {
            responseMap.put("error", true);
            responseMap.put("text", "You should log in first");
        }
        else {
            Operator user = operatorService.getUser(login);
            if(user == null) {
                responseMap.put("error", true);
                responseMap.put("text", "Your account does not exist");
            }
            else {
                Article article = articleService.getById(article_id);
                if(article == null) {
                    responseMap.put("error", true);
                    responseMap.put("text", "Article does not exist. Please, reload the page.");
                }
                else if(article.getNum() < num) {
                    responseMap.put("error", true);
                    responseMap.put("text", "Out of stock");
                }
                else {
                    responseMap.put("error", false);
                    BasketItem tryFind = basketService.getByArticle(user.getID(), article.getId());
                    if(tryFind == null)
                        basketService.add(new BasketItem(-1, user.getID(), article, num));
                    else
                        basketService.update(new BasketItem(tryFind.getId(), user.getID(), article, tryFind.getNum() + num));
                }
            }
        }
        return responseMap;
    }

    @RequestMapping(value = "/delete_from_basket", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteFromBasket(@RequestBody String param) {
        Integer id = Integer.valueOf(param.split("=")[1]);
        BasketItem item = basketService.getById(id);
        Map<String, Object> responseMap = new HashMap<String, Object>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();
        if(login.equals("anonymousUser")) {
            responseMap.put("error", true);
            responseMap.put("text", "You should log in first");
        }
        else {
            Operator user = operatorService.getUser(login);
            if(user == null || item == null || user.getID() != item.getUserID()) {
                responseMap.put("error", true);
                responseMap.put("text", "You have no access to delete this item from the basket");
            }
            else {
                responseMap.put("error", false);
                basketService.deleteByID(item.getId());
            }
        }
        return responseMap;
    }

    @RequestMapping(value = "/check_for_order", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkForOrder(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = operatorService.getUser(auth.getName()).getID();
        String result = basketService.checkForOrder(userID);
        Map<String, Object> responseMap = new HashMap<String, Object>();

        if(result != null) {
            responseMap.put("error", true);
            responseMap.put("text", result);
        }
        else {
            responseMap.put("error", false);
        }
        return responseMap;
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> makeOrder(@RequestParam("name") String name,
                                         @RequestParam("email") String email,
                                         @RequestParam("telephone") String telephone,
                                         @RequestParam("address") String address,
                                         @RequestParam("pay_type") String pay_type) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        Integer userID = user.getID();
        String result = basketService.checkForOrder(userID);
        Map<String, Object> responseMap = new HashMap<String, Object>();

        if(result != null) {
            responseMap.put("error", true);
            responseMap.put("text", result);
        }
        else {
            basketService.makeOrder(user, name, email, telephone, address, pay_type);
            basketService.deleteByUserID(userID);
            responseMap.put("error", false);
        }
        return responseMap;
    }
}
