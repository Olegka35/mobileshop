package oper_project.domain;

public class OrderItem {
    private String articleTypeName;
    private Integer price;
    private Integer num;

    public OrderItem(String article_type_name, Integer price, Integer num) {
        this.articleTypeName = article_type_name;
        this.price = price;
        this.num = num;
    }

    public String getArticleTypeName() {
        return articleTypeName;
    }

    public void setArticleTypeName(String articleTypeName) {
        this.articleTypeName = articleTypeName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
