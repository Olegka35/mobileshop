package oper_project.service;

import oper_project.domain.Operator;

import java.util.List;
import java.util.Map;

/**
 * Created by Олег on 30.03.2017.
 */
public interface OperatorService {
    void add(Operator user);
    void update(Operator user);
    void delete(Integer id);
    Operator getById(int id);
    Operator getUser(String login);

    List<Map<String, Object>> getArticleParams();
    List<Map<String, Object>> getVarArticleParams();
    void addArticleParam(String type);
    boolean deleteArticleParam(Integer id);
    Integer getAttrIDByName(String attr);
}
