package oper_project.controller;


import oper_project.domain.Article;
import oper_project.domain.html_requests.ArticleList;

import oper_project.service.ArticleService;
import oper_project.service.BasketService;
import oper_project.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Олег on 09.06.2018.
 */

@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final OperatorService operatorService;
    private final BasketService basketService;

    @Autowired
    public ArticleController(ArticleService articleService, OperatorService operatorService, BasketService basketService) {
        this.articleService = articleService;
        this.operatorService = operatorService;
        this.basketService = basketService;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView loadIndexPage(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "sort", required = false) String sort,
                                     @RequestParam(value = "reversed", required = false) Boolean reversed,
                                     @RequestParam(value = "search", required = false) String search) {
        ArticleList articleList = new ArticleList();
        articleList.setArticleList(articleService.getArticleList(1, null, null, null, null));
        articleList.setPage(1);
        articleList.setPageNumEx(articleService.getArticlesNum(search));
        articleList.setParams(operatorService.getVarArticleParams());
        return new ModelAndView("index", "articleList", articleList);
    }

    @RequestMapping(value = "/get_articles", method = RequestMethod.POST)
    public @ResponseBody
    ArticleList displayArticles(@RequestParam(value = "page", required = false) Integer page,
                                        @RequestParam(value = "sort", required = false) String sort,
                                        @RequestParam(value = "reversed", required = false) Boolean reversed,
                                        @RequestParam(value = "search", required = false) String search,
                                        @RequestParam(value = "filter", required = false) Boolean hideEmpty) {
        if (page == null) page = 1;
        if (sort != null && sort.equals("object_id") && !reversed)
            sort = null;
        if(hideEmpty == false) hideEmpty = null;

        ArticleList articleList = new ArticleList();
        articleList.setArticleList(articleService.getArticleList(page, sort, reversed, search, hideEmpty));
        articleList.setPage(page);
        articleList.setPageNumEx(articleService.getArticlesNum(search));
        return articleList;
    }

    @RequestMapping(value = "/article", method = RequestMethod.POST)
    public
    @ResponseBody
    Article getArticleInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody String id) {
        return articleService.getById(Integer.parseInt(id.split("=")[1]));
    }

    @RequestMapping(value = "/index/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteArticle(HttpServletRequest request, @RequestParam("id") Integer articleId) {
        Map<String, Object> map = new HashMap<>();
        if(request.isUserInRole("ROLE_ADMIN")) {
            basketService.deleteByArticle(articleId);
            articleService.delete(articleId);
            map.put("error", false);
        }
        else {
            map.put("error", true);
            map.put("text", "Access denied");
        }
        return map;
    }

    @RequestMapping(value = "/index/edit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateArticle(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        if(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")) {
            Map<String, String[]> map = request.getParameterMap();
            Map<String, Object> details = new HashMap<String, Object>();

            Article article;
            try {
                article = articleService.getById(Integer.parseInt(map.get("ID")[0]));
            }
            catch (Exception e) {
                article = null;
            }
            if (article != null) {
                for (Map.Entry entry : map.entrySet()) {
                    String key = entry.getKey().toString();
                    if (!Arrays.asList("Type", "Num", "Price").contains(key)) {
                        details.put(key, map.get(key)[0]);
                    }
                    article.setDetails(details);
                }
                article.setType(map.get("Type")[0]);
                article.setPrice(Integer.parseInt(map.get("Price")[0]));
                article.setNum(Integer.parseInt(map.get("Num")[0]));
                articleService.update(article);
                resultMap.put("error", false);
            }
            else {
                resultMap.put("error", true);
                resultMap.put("text", "Article does not exist");
            }
        }
        else {
            resultMap.put("error", true);
            resultMap.put("text", "Access denied");
        }
        return resultMap;
    }

    @RequestMapping(value = "/index/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addArticle(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        if(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")) {
            Map<String, String[]> map = request.getParameterMap();
            Map<String, Object> details = new HashMap<String, Object>();
            Integer price = null, num = null;
            for (Map.Entry entry : map.entrySet()) {
                String key = entry.getKey().toString();
                if (!Arrays.asList("Type", "Num", "Price").contains(key)) {
                    details.put(key, map.get(key)[0]);
                }
            }
            price = Integer.parseInt(map.get("Price")[0]);
            num = Integer.parseInt(map.get("Num")[0]);
            if (price < 0) price = 0;
            if (num < 0) num = 0;
            Article article = new Article(-1, map.get("Type")[0], details, num, price);
            articleService.add(article);
            resultMap.put("error", false);
        }
        else {
            resultMap.put("error", true);
            resultMap.put("text", "Access denied");
        }
        return resultMap;
    }
}
