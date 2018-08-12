package oper_project.service;

import oper_project.domain.Article;

import java.util.List;

/**
 * Created by Олег on 08.06.2018.
 */
public interface ArticleService {
    void add(Article article);
    void update(Article article);
    void delete(Integer id);
    Article getById(int id);
    List<Article> getArticleList();
    List<Article> getArticleList(Integer page);
    List<Article> getArticleList(String search);
    List<Article> getArticleList(Integer page, String search);
    List<Article> getArticleList(Integer page, String order_field, Boolean reversed, String search, Boolean hideEmpty);
    Integer getArticlesNum();
    Integer getArticlesNum(String search);
}
