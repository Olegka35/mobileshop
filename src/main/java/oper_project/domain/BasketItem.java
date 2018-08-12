package oper_project.domain;

import oper_project.service.impl.ArticleServiceImpl;

public class BasketItem {
    private Integer id;
    private Integer userID;
    private Article article;
    private Integer num;

    public BasketItem() {
    }

    public BasketItem(Integer id, Integer userID, Article article, Integer num) {
        this.id = id;
        this.userID = userID;
        this.article = article;
        this.num = num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "BasketItem{" +
                "id=" + id +
                ", userID=" + userID +
                ", article=" + article +
                ", num=" + num +
                '}';
    }
}
