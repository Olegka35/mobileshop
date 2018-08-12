package oper_project.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by Олег on 08.06.2018.
 */
public interface DAO {
    Integer add(String type, Map<String, String> params, String name_field);
    void addAttr(String type, String attribute);
    void update(Integer id, Map<String, String> params);
    void delete(Integer id);
    void deleteAttr(Integer id);
    Map<String, Object> getById(int id);
    List<Integer> getIDs(String type);
    List<Integer> getIDs(String type, Map<String, String> filterMap);
    List<Map<String, Object>> getList(String type);
    List<Map<String, Object>> getList(String type, Integer first, Integer num);
    List<Map<String, Object>> getList(String type, Map<String, String> filterMap);
    List<Map<String, Object>> getList(String type, Integer first, Integer num, Map<String, String> filterMap);
    List<Map<String, Object>> getList(String type, Integer first, Integer num, String order_field, Boolean reversed, Map<String, String> filterMap);
    Integer getIdByParam(String type, String param, String value);
    List<Integer> getIDsByParam(String type, String param, String value);
    List<Integer> getIDsByParam(String type, String param, String value, Integer first, Integer num);
    List<Integer> getIDsByParam(String type, String param, String value, Integer first, Integer num, String order_field, Boolean reversed, Map<String, String> filterMap);
    List<Integer> getIDsByParam(String type, String param, String value, Map<String, String> filterMap);
    List<Map<String, Object>> getAttrs(String type);
    Integer getAttrIDByName(String type, String attribute);
    String getAttrNameByID(String type, Integer id);
}
