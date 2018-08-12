package oper_project.controller;

import oper_project.domain.BasketItem;
import oper_project.domain.Operator;
import oper_project.domain.Order;
import oper_project.domain.html_requests.AddBasketItem;
import oper_project.domain.html_requests.MakeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import oper_project.domain.Article;
import oper_project.service.ArticleService;
import oper_project.service.BasketService;
import oper_project.service.OperatorService;
import oper_project.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class RestController {
    private final ArticleService articleService;
    private final OperatorService operatorService;
    private final BasketService basketService;
    private final OrderService orderService;

    @Autowired
    public RestController(ArticleService articleService, OperatorService operatorService, BasketService basketService, OrderService orderService) {
        this.articleService = articleService;
        this.operatorService = operatorService;
        this.basketService = basketService;
        this.orderService = orderService;
    }

    /* articles */
    @RequestMapping(method = RequestMethod.GET, value = "/rest/articles", produces = "application/json")
    public ResponseEntity<List<Article>> restGetArticles() {
        List<Article> articleList = articleService.getArticleList();
        if(articleList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(articleList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rest/articles", consumes = "application/json")
    public ResponseEntity<Article> restUpdateArticle(HttpServletRequest request, @RequestBody Article article) {
        if(!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_MANAGER"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if(article.getId() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Article current = articleService.getById(article.getId());
        if(current == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(article.getNum() != null && article.getNum() < 0 || article.getPrice() != null && article.getPrice() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        articleService.update(article);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rest/articles", consumes = "application/json")
    public ResponseEntity<Article> restAddArticle(HttpServletRequest request, @RequestBody Article article) {
        if(!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_MANAGER"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if(article.getType() == null || article.getNum() == null || article.getNum() < 0 || article.getPrice() == null || article.getPrice() < 0 || article.getDetails() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        articleService.add(article);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rest/articles/{id}", produces = "application/json")
    public ResponseEntity<Article> restGetArticle(@PathVariable("id") Integer id) {
        Article article = articleService.getById(id);
        if(article == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/rest/articles/{id}", produces = "application/json")
    public ResponseEntity<Article> restDeleteArticle(HttpServletRequest request, @PathVariable("id") int id) {
        if(!request.isUserInRole("ROLE_ADMIN"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Article article = articleService.getById(id);
        if(article == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        articleService.delete(id);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    /* basket */
    @RequestMapping(method = RequestMethod.GET, value = "/rest/basket", produces = "application/json")
    public ResponseEntity<List<BasketItem>> restGetOrdersInBasket() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<BasketItem> basketItems = basketService.getByUserID(user.getID());
        if(basketItems.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(basketItems, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rest/basket", consumes = "application/json")
    public ResponseEntity<BasketItem> restAddBasketItem(HttpServletRequest request, @RequestBody AddBasketItem addBasketItem) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if(addBasketItem.getNum() == null || addBasketItem.getNum() <= 0 || addBasketItem.getArticle_id() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Article article = articleService.getById(addBasketItem.getArticle_id());
        if(article == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        BasketItem basketItem = new BasketItem(-1, user.getID(), article, addBasketItem.getNum());
        BasketItem tryFind = basketService.getByArticle(user.getID(), article.getId());
        if(tryFind == null)
            basketService.add(basketItem);
        else {
            basketItem.setNum(tryFind.getNum() + basketItem.getNum());
            basketItem.setId(tryFind.getId());
            basketService.update(basketItem);
        }
        return new ResponseEntity<>(basketItem, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/rest/basket/{id}", produces = "application/json")
    public ResponseEntity<BasketItem> restDeleteFromBasket(@PathVariable("id") int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Article article = articleService.getById(id);
        if(article == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        BasketItem basketItem = basketService.getByArticle(user.getID(), article.getId());
        if(basketItem == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        basketService.deleteByID(basketItem.getId());
        return new ResponseEntity<>(basketItem, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rest/order", consumes = "application/json")
    public ResponseEntity<Order> restMakeOrder(@RequestBody MakeOrder order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if(order.getAddress() == null || order.getEmail() == null || order.getName() == null || order.getNumber() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String result = basketService.checkForOrder(user.getID());
        if(result != null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        basketService.makeOrder(user, order.getName(), order.getEmail(), order.getNumber(), order.getAddress(), order.getPayType());
        basketService.deleteByUserID(user.getID());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* orders */
    @RequestMapping(method = RequestMethod.GET, value = "/rest/myorders", produces = "application/json")
    public ResponseEntity<List<Order>> restGetMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Operator user = operatorService.getUser(auth.getName());
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<Order> myOrders = orderService.getUserOrderList(user.getID());
        if(myOrders.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(myOrders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rest/orders", produces = "application/json")
    public ResponseEntity<List<Order>> restGetOrders(HttpServletRequest request) {
        if(!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_MANAGER"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<Order> orderList = orderService.getOrderList();
        if(orderList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rest/orders/{id}", produces = "application/json")
    public ResponseEntity<Order> restGetOrder(HttpServletRequest request, @PathVariable("id") int id) {
        if(!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_MANAGER"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Order order = orderService.getById(id);
        if(order == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rest/order_status", consumes = "application/json")
    public ResponseEntity<Order> restOrderStatus(HttpServletRequest request, @RequestParam("order_id") Integer orderID,  @RequestParam("status") Integer status) {
        if(!request.isUserInRole("ROLE_ADMIN") && !request.isUserInRole("ROLE_MANAGER"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if(status < 0 || status > 2)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Order order = orderService.getById(orderID);
        if(order == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        orderService.updateStatus(orderID, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* users */
    @RequestMapping(method = RequestMethod.GET, value = "/rest/users/{Login}", produces = "application/json")
    public ResponseEntity<Operator> restGetOrder(HttpServletRequest request, @PathVariable("Login") String Login) {
        if(!request.isUserInRole("ROLE_ADMIN"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Operator user = operatorService.getUser(Login);
        if(user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rest/user_role", consumes = "application/json")
    public ResponseEntity<Operator> restGetOrder(HttpServletRequest request, @RequestBody Operator user2) {
        if(!request.isUserInRole("ROLE_ADMIN"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Operator user = operatorService.getUser(user2.getLogin());
        if(user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        user.setRole(user2.getRole());
        operatorService.update(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
