package oper_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import oper_project.domain.Article;
import oper_project.domain.Operator;
import oper_project.service.ArticleService;
import oper_project.service.OperatorService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Олег on 08.06.2018.
 */

@Controller
public class OperatorController {
    private final OperatorService operatorService;
    private final ArticleService articleService;

    @Autowired
    public OperatorController(OperatorService operatorService, ArticleService articleService) {
        this.operatorService = operatorService;
        this.articleService = articleService;
    }

    @ModelAttribute("operatorBean")
    public Operator createOperatorBean() {
        return new Operator();
    }

    @ModelAttribute("articleBean")
    public Article createArticleBean() {
        return new Article();
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/reg", method = RequestMethod.GET)
    public ModelAndView regPage() {
        return new ModelAndView("register");
    }

    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    public ModelAndView regUser(@RequestParam("login") String login,
                                @RequestParam("password") String password) {
        if (operatorService.getUser(login) != null) {
            return new ModelAndView("regerror");
        }
        Operator user = new Operator(-1, login, password, "ROLE_USER");
        operatorService.add(user);
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/params", method = RequestMethod.GET)
    public ModelAndView operatorPage() {
        List<Map<String, Object>> params = operatorService.getArticleParams();
        return new ModelAndView("params", "params", params);
    }

    @RequestMapping(value = "/params/addparam", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addParam(@RequestParam("name") String name) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        if (operatorService.getAttrIDByName(name) != null) {
            responseMap.put("error", true);
            responseMap.put("text", "Attribute is already exist");
        }
        else {
            operatorService.addArticleParam(name);
            responseMap.put("error", false);
        }
        return responseMap;
    }

    @RequestMapping(value = "/params/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteParam(HttpServletRequest request, @RequestParam("id") Integer id) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        if(!request.isUserInRole("ROLE_ADMIN")) {
            responseMap.put("text", "Forbidden");
            responseMap.put("error", true);
        }
        else {
            if (operatorService.deleteArticleParam(id)) {
                responseMap.put("error", false);
            }
            else {
                responseMap.put("text", "This attribute cannot be deleted");
                responseMap.put("error", true);
            }
        }
        return responseMap;
    }

}
