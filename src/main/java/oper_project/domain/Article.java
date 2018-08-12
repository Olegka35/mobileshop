package oper_project.domain;

import java.util.Map;

/**
 * Created by Олег on 08.06.2018.
 */
public class Article {
    private Integer id;
    private String type;
    private Map<String, Object> details;
    private Integer num;
    private Integer price;

    public Article() {
    }

    public Article(String id) {
        this.id = Integer.parseInt(id);
    }

    public Article(Integer id, String type, Map<String, Object> details, Integer num, Integer price) {
        this.id = id;
        this.type = type;
        this.details = details;
        this.num = num;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", details=" + details +
                ", num=" + num +
                ", price=" + price +
                '}';
    }
}
