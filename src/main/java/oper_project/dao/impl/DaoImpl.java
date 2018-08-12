package oper_project.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import oper_project.dao.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Олег on 08.06.2018.
 */

@Repository("DAO")
public class DaoImpl implements DAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer add(final String type, final Map<String, String> params, final String name_field) {
        String ADD_PARAMS = "INSERT INTO params VALUES (?, (SELECT attribute_id FROM attribute WHERE attribute_name = ?), ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    String ADD_QUERY = "INSERT INTO object VALUES (0, (SELECT object_type_id FROM object_type WHERE type_name = ?), ?)";
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(ADD_QUERY, new String[] {"object_id"});
                        ps.setString(1, type);
                        ps.setString(2, params.get(name_field));
                        return ps;
                    }
                },
                keyHolder);


        Integer insert_id = keyHolder.getKey().intValue();
        if(params != null) {
            for (Map.Entry<String, String> pair : params.entrySet()) {
                jdbcTemplate.update(ADD_PARAMS, insert_id, pair.getKey(), pair.getValue());
            }
        }
        return insert_id;
    }

    @Override
    public void addAttr(String type, final String attribute) {
        final String ADD_ATTR = "INSERT INTO attribute VALUES (0, ?)";
        final String TIE_ATTR = "INSERT INTO attribute_object VALUES ((SELECT object_type_id FROM object_type WHERE type_name = ?), ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(ADD_ATTR, new String[] {"object_id"});
                ps.setString(1, attribute);
                return ps;
            }
        }, keyHolder);
        jdbcTemplate.update(TIE_ATTR, type, keyHolder.getKey());
    }

    @Override
    public void update(Integer id, Map<String, String> params) {
        String UPDATE_QUERY = "REPLACE INTO params VALUES (?, (SELECT attribute_id FROM attribute WHERE attribute_name = ?), ?)";
        for (Map.Entry<String, String> pair: params.entrySet()) {
            jdbcTemplate.update(UPDATE_QUERY, id, pair.getKey(), pair.getValue());
        }
    }

    @Override
    public void delete(Integer id) {
        String DELETE_OBJECT_QUERY = "DELETE FROM object WHERE object_id = ?";
        String DELETE_PARAMS_QUERY = "DELETE FROM params WHERE object_id = ?";
        jdbcTemplate.update(DELETE_PARAMS_QUERY, id);
        jdbcTemplate.update(DELETE_OBJECT_QUERY, id);
    }

    @Override
    public void deleteAttr(Integer id) {
        String DELETE_ATTR_QUERY = "DELETE FROM attribute WHERE attribute_id = ?";
        String DELETE_ATTR_OBJ_QUERY = "DELETE FROM attribute_object WHERE attribute_id = ?";
        String DELETE_PARAMS_QUERY = "DELETE FROM params WHERE attribute_id = ?";
        jdbcTemplate.update(DELETE_ATTR_OBJ_QUERY, id);
        jdbcTemplate.update(DELETE_PARAMS_QUERY, id);
        jdbcTemplate.update(DELETE_ATTR_QUERY, id);
    }

    @Override
    public Map<String, Object> getById(int id) {
        String SELECT_QUERY = "SELECT at.attribute_name AS name, val FROM params JOIN attribute AS at ON at.attribute_id = params.attribute_id WHERE object_id = ?";
        List<Map<String, Object>> queryList = jdbcTemplate.queryForList(SELECT_QUERY, id);

        if(queryList.size() == 0)
            return null;

        Map<String, Object> map = new HashMap<String, Object>();
        for(Map<String, Object> tempMap: queryList) {
            map.put(tempMap.get("name").toString(), tempMap.get("val"));
        }
        map.put("ID", id);
        return map;
    }

    @Override
    public List<Integer> getIDs(String type) {
        return jdbcTemplate.queryForList("SELECT object_id FROM object AS obj WHERE object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)", Integer.class, type);
    }

    @Override
    public List<Integer> getIDs(String type, Map<String, String> filterMap) {
        StringBuilder SELECT_QUERY = new StringBuilder(filterQuery("SELECT obj.object_id FROM object AS obj ", filterMap));
        SELECT_QUERY.append("WHERE object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)");
        return jdbcTemplate.queryForList(SELECT_QUERY.toString(), Integer.class, type);
    }

    private List<Map<String, Object>> getListEx(List<Integer> ids)
    {
        List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        for(Integer id: ids) {
            list.add(getById(id));
        }
        return list;
    }

    public List<Map<String, Object>> getList(String type) {
        List<Integer> ids = getIDs(type);
        return getListEx(ids);
    }

    public List<Map<String, Object>> getList(String type, Integer first, Integer num) {
        List<Integer> ids = jdbcTemplate.queryForList("SELECT object_id FROM object WHERE object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?) LIMIT ?, ?", Integer.class, type, first, num);
        return getListEx(ids);
    }

    public List<Map<String, Object>> getList(String type, Map<String, String> filterMap) {
        StringBuilder SELECT_QUERY = new StringBuilder(filterQuery("SELECT obj.object_id FROM object AS obj ", filterMap));
        SELECT_QUERY.append("WHERE obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)");
        List<Integer> ids = jdbcTemplate.queryForList(SELECT_QUERY.toString(), Integer.class, type);
        return getListEx(ids);
    }

    public List<Map<String, Object>> getList(String type, Integer first, Integer num, Map<String, String> filterMap) {
        StringBuilder SELECT_QUERY = new StringBuilder(filterQuery("SELECT obj.object_id FROM object AS obj ", filterMap));
        SELECT_QUERY.append("WHERE obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?) LIMIT ?, ?");
        List<Integer> ids = jdbcTemplate.queryForList(SELECT_QUERY.toString(), Integer.class, type, first, num);
        return getListEx(ids);
    }

    @Override
    public List<Map<String, Object>> getList(String type, Integer first, Integer num, String order_field, Boolean reversed, Map<String, String> filterMap) {
        StringBuilder SELECT_QUERY = new StringBuilder("SELECT DISTINCT obj.object_id FROM object AS obj JOIN params ON obj.object_id = params.object_id ");
        List<Integer> ids;
        /* Поле сортировки */
        if(order_field != null && !order_field.equals("object_id"))
            SELECT_QUERY.append("LEFT JOIN params AS extra ON extra.object_id = params.object_id AND extra.attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) ");

        // Фильтрация
        if(filterMap != null)
            SELECT_QUERY = new StringBuilder(filterQuery(SELECT_QUERY.toString(), filterMap));

        SELECT_QUERY.append("WHERE obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?) ");

        /* Порядок сортировки */
        if(order_field != null) {
            if(type.equals("article") && Arrays.asList("Num", "Price").contains(order_field) ||
                    type.equals("order") && order_field.equals("Order_Price")) {
                SELECT_QUERY.append("ORDER BY CONVERT(extra.val, UNSIGNED)");
            }
            else if (!order_field.equals("object_id"))
                SELECT_QUERY.append("ORDER BY extra.val");
            else
                SELECT_QUERY.append("ORDER BY params.object_id");
            if (reversed)
                SELECT_QUERY.append(" DESC");
        }

        /* Пагинация */
        SELECT_QUERY.append(" LIMIT ?, ?");

        if(order_field != null && !order_field.equals("object_id"))
            ids = jdbcTemplate.queryForList(SELECT_QUERY.toString(), Integer.class, order_field, type, first, num);
        else
            ids = jdbcTemplate.queryForList(SELECT_QUERY.toString(), Integer.class, type, first, num);
        return getListEx(ids);
    }

    public Integer getIdByParam(String type, String param, String value) {
        try {
            String SELECT_QUERY = "SELECT obj.object_id FROM object AS obj JOIN params ON obj.object_id = params.object_id WHERE attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) AND val = ? AND obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?) LIMIT 1";
            return jdbcTemplate.queryForObject(SELECT_QUERY, Integer.class, param, value, type);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Integer> getIDsByParam(String type, String param, String value) {
        String SELECT_QUERY = "SELECT obj.object_id FROM object AS obj JOIN params ON obj.object_id = params.object_id WHERE attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) AND val LIKE ? AND obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)";
        return jdbcTemplate.queryForList(SELECT_QUERY, Integer.class, param, value, type);
    }

    public List<Integer> getIDsByParam(String type, String param, String value, Integer first, Integer num) {
        String SELECT_QUERY = "SELECT obj.object_id FROM object AS obj " +
                "JOIN params ON obj.object_id = params.object_id " +
                "WHERE attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) AND val LIKE ? AND obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?) LIMIT ?, ?";
        return jdbcTemplate.queryForList(SELECT_QUERY, Integer.class, param, value, type, first, num);
    }

    public List<Integer> getIDsByParam(String type, String param, String value, Integer first, Integer num, String order_field, Boolean reversed, Map<String, String> filterMap) {
        String SELECT_QUERY = "SELECT DISTINCT obj.object_id FROM object AS obj " +
                "JOIN params ON obj.object_id = params.object_id ";

        /* Поле сортировки */
        if(order_field != null && !order_field.equals("object_id"))
            SELECT_QUERY += "LEFT JOIN params AS extra ON extra.object_id = params.object_id AND extra.attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) ";

        // Фильтрация
        if(filterMap != null)
            SELECT_QUERY = filterQuery(SELECT_QUERY, filterMap);

        SELECT_QUERY += "WHERE params.attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) AND params.val LIKE ? AND obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?) ";

        /* Порядок сортировки */
        if(order_field != null) {
            if (!order_field.equals("object_id"))
                SELECT_QUERY += "ORDER BY extra.val";
            else
                SELECT_QUERY += "ORDER BY params.object_id";
            if (reversed)
                SELECT_QUERY += " DESC";
        }

        /* Пагинация */
        SELECT_QUERY += " LIMIT ?, ?";

        if(order_field != null && !order_field.equals("object_id"))
            return jdbcTemplate.queryForList(SELECT_QUERY, Integer.class, order_field, param, value, type, first, num);
        else
            return jdbcTemplate.queryForList(SELECT_QUERY, Integer.class, param, value, type, first, num);
    }

    public List<Integer> getIDsByParam(String type, String param, String value, Map<String, String> filterMap) {
        String SELECT_QUERY = "SELECT DISTINCT obj.object_id FROM object AS obj " +
                "JOIN params ON obj.object_id = params.object_id ";

        // Фильтрация
        if(filterMap != null)
            SELECT_QUERY = filterQuery(SELECT_QUERY, filterMap);

        SELECT_QUERY += "WHERE params.attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = ?) AND params.val LIKE ? AND obj.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)";
        return jdbcTemplate.queryForList(SELECT_QUERY, Integer.class, param, value, type);
    }

    @Override
    public List<Map<String, Object>> getAttrs(String type) {
        String SELECT_QUERY = "SELECT attribute_id, attribute_name FROM attribute WHERE attribute_id IN (SELECT attribute_id FROM attribute_object WHERE object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?))";
        List<Map<String, Object>> queryList = jdbcTemplate.queryForList(SELECT_QUERY, type);

        if(queryList.size() == 0)
            return null;
        return queryList;
    }

    @Override
    public Integer getAttrIDByName(String type, String attribute) {
        try {
            String SELECT_QUERY = "SELECT a.attribute_id attribute_id FROM attribute a JOIN attribute_object ao ON ao.attribute_id = a.attribute_id WHERE a.attribute_name = ? AND ao.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)";
            return jdbcTemplate.queryForObject(SELECT_QUERY, Integer.class, attribute, type);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String getAttrNameByID(String type, Integer id) {
        try {
            String SELECT_QUERY = "SELECT a.attribute_name attribute_name FROM attribute a JOIN attribute_object ao ON ao.attribute_id = a.attribute_id WHERE a.attribute_id = ? AND ao.object_type_id = (SELECT object_type_id FROM object_type WHERE type_name = ?)";
            return jdbcTemplate.queryForObject(SELECT_QUERY, String.class, id, type);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    private String filterQuery(String SELECT_QUERY, Map<String, String> filterMap)
    {
        StringBuilder str = new StringBuilder(SELECT_QUERY);
        int temp = 1;
        for(Map.Entry<String, String> filter: filterMap.entrySet()) {
            String filterField = "filter" + temp++;
            String key = filter.getKey();
            if(key.contains("!!!LIST!!!")) {
                key = key.replace("!!!LIST!!!", "");
                str.append("JOIN params AS ").append(filterField).append(" ON ").append(filterField).append(".object_id = obj.object_id AND ").append(filterField);
                str.append(".attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = '").append(key).append("') AND ").append(filterField).append(".val IN (").append(filter.getValue()).append(") ");
            }
            else if(key.contains("!!!NOT_ZERO!!!")) {
                key = key.replace("!!!NOT_ZERO!!!", "");
                str.append("JOIN params AS ").append(filterField).append(" ON ").append(filterField).append(".object_id = obj.object_id AND ").append(filterField);
                str.append(".attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = '").append(key).append("') AND ").append(filterField).append(".val <> '0' ");

            }
            else {
                str.append("JOIN params AS ").append(filterField).append(" ON ").append(filterField).append(".object_id = obj.object_id AND ").append(filterField);
                str.append(".attribute_id = (SELECT attribute_id FROM attribute WHERE attribute_name = '").append(filter.getKey()).append("') AND ").append(filterField).append(".val LIKE '%").append(filter.getValue()).append("%' ");
            }
        }
        return str.toString();
    }
}
