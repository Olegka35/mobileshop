package oper_project.service.impl;

import oper_project.domain.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import oper_project.configuration.SpringJDBCConfiguration;
import oper_project.dao.DAO;
import oper_project.service.BasketService;

import java.util.*;

@Service("basketService")
public class BasketServiceImpl implements BasketService {
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringJDBCConfiguration.class);
    private DAO dao = context.getBean(DAO.class);

    @Override
    public void add(BasketItem item) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Basket_UserID", item.getUserID().toString());
        params.put("Basket_ArticleID", item.getArticle().getId().toString());
        params.put("Basket_Num", item.getNum().toString());
        dao.add("basket_item", params, "Basket_ArticleID");
    }

    @Override
    public void deleteByID(Integer id) {
        dao.delete(id);
    }

    @Override
    public void deleteByUserID(Integer user_id) {
        List<BasketItem> list = getByUserID(user_id);
        for(BasketItem item: list) {
            deleteByID(item.getId());
        }
    }

    @Override
    public void deleteByArticle(Integer id) {
        List<Integer> ids = dao.getIDsByParam("basket_item", "Basket_ArticleID", id.toString());
        if(ids != null) {
            for(Integer item_id: ids) dao.delete(item_id);
        }
    }

    @Override
    public void update(BasketItem item) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Basket_UserID", item.getUserID().toString());
        params.put("Basket_ArticleID", item.getArticle().getId().toString());
        params.put("Basket_Num", item.getNum().toString());
        dao.update(item.getId(), params);
    }

    @Override
    public BasketItem getById(Integer id) {
        Map<String, Object> map = dao.getById(id);
        if(map == null) return null;
        return new BasketItem(Integer.parseInt(map.get("ID").toString()), Integer.parseInt(map.get("Basket_UserID").toString()),
                new ArticleServiceImpl().getById(Integer.parseInt(map.get("Basket_ArticleID").toString())), Integer.parseInt(map.get("Basket_Num").toString()));
    }

    @Override
    public BasketItem getByArticle(Integer userID, Integer articleID) {
        List<Integer> ids = dao.getIDsByParam("basket_item", "Basket_UserID", userID.toString());
        for(Integer id: ids) {
            BasketItem basketItem = getById(id);
            if(basketItem.getArticle().getId() == articleID)
                return basketItem;
        }
        return null;
    }

    @Override
    public List<BasketItem> getByUserID(Integer id) {
        List<Integer> ids = dao.getIDsByParam("basket_item", "Basket_UserID", id.toString());
        List<BasketItem> items = new ArrayList<BasketItem>();
        if(ids != null) {
            for(Integer item_id: ids) items.add(getById(item_id));
        }
        return items;
    }

    @Override
    public String checkForOrder(Integer userID) {
        List<BasketItem> items = getByUserID(userID);
        if(items.size() == 0) return "Your basket is empty";
        for(BasketItem item: items) {
            if(item.getNum() > item.getArticle().getNum()) return "This amount of " + item.getArticle().getType() + " is unavailable in the stock.";
        }
        return null;
    }

    @Override
    public Integer getPrice(Integer userID) {
        Integer price = 0;
        List<BasketItem> basketItems = getByUserID(userID);
        for(BasketItem item: basketItems) {
            price += item.getNum() * item.getArticle().getPrice();
        }
        return price;
    }

    @Override
    public void makeOrder(Operator user, String name, String email, String telephone, String address, String pay_type) {
        List<OrderItem> details = new ArrayList<>();
        List<BasketItem> basketItems = getByUserID(user.getID());
        for(BasketItem item: basketItems) {
            Article article = item.getArticle();
            details.add(new OrderItem(article.getType(), article.getPrice(), item.getNum()));
            article.setNum(article.getNum() - item.getNum());
            new ArticleServiceImpl().update(article);
        }
        new OrderServiceImpl().addOrder(new Order(-1, user, name, email, address, telephone, new Date(), new BasketServiceImpl().getPrice(user.getID()), 0, pay_type, details));
    }
}
