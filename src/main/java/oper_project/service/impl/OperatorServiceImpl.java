package oper_project.service.impl;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import oper_project.configuration.SpringJDBCConfiguration;
import oper_project.dao.DAO;
import oper_project.domain.Operator;
import oper_project.service.OperatorService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Олег on 30.03.2017.
 */

@Service("operatorService")
public class OperatorServiceImpl implements OperatorService {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringJDBCConfiguration.class);
    private DAO dao = context.getBean(DAO.class);

    public void add(Operator user) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("login", user.getLogin());
        params.put("password", user.getPassword());
        params.put("role", user.getRole());
        dao.add("operator", params, "login");
    }

    public void update(Operator user) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("login", user.getLogin());
        params.put("password", user.getPassword());
        params.put("role", user.getRole());

        dao.update(user.getID(), params);
    }

    public void delete(Integer id) {
        dao.delete(id);
    }

    public Operator getById(int id) {
        Map<String, Object> map = dao.getById(id);
        if(map == null) return null;
        return new Operator(Integer.parseInt(map.get("ID").toString()), map.get("login").toString(), map.get("password").toString(), map.get("role").toString());
    }

    public Operator getUser(String login) {
        Integer id = dao.getIdByParam("operator", "login", login);
        if(id != null)
            return getById(id);
        else
            return null;
    }

    @Override
    public List<Map<String, Object>> getArticleParams() {
        return dao.getAttrs("article");
    }

    @Override
    public List<Map<String, Object>> getVarArticleParams() {
        List<Map<String, Object>> list = dao.getAttrs("article");
        Iterator<Map<String, Object>> iterator = list.iterator();
        while (iterator.hasNext())  {
            Map<String, Object> map = iterator.next();
            if(Arrays.asList("Type", "Num", "Price").contains(map.get("attribute_name"))) {
                iterator.remove();
            }
        }
        return list;
    }

    @Override
    public void addArticleParam(String type) {
        dao.addAttr("article", type);
    }

    @Override
    public boolean deleteArticleParam(Integer id) {
        if(Arrays.asList("Type", "Num", "Price").contains(dao.getAttrNameByID("article", id)))
            return false;
        dao.deleteAttr(id);
        return true;
    }

    @Override
    public Integer getAttrIDByName(String attr) {
        return dao.getAttrIDByName("article", attr);
    }
}
