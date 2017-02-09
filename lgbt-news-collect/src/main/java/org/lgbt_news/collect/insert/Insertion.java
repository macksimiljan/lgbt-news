package org.lgbt_news.collect.insert;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * @author max
 */
public class Insertion {

    protected final Connection CONN;
    protected PreparedStatement prepStat;

    public Insertion(Connection conn) {
        CONN = conn;
    }

    protected void setCharValue(int index, JSONObject obj, String key) throws Exception  {
        if (obj.has(key) && !JSONObject.NULL.equals(obj.get(key))) {
            String value = obj.getString(key).trim();
            setStringValue(index, value, Types.CHAR);
        } else
            setNullValue(index, Types.CHAR);
    }

    protected void setNVarcharValue(int index, JSONObject obj, String key) throws Exception {
        setNVarcharValue(index, obj, key, -1);
    }

    protected void setNVarcharValue(int index, JSONObject obj, String key, int maxLength) throws Exception {
        if (obj.has(key) && !JSONObject.NULL.equals(obj.get(key))) {
            String value = obj.getString(key).trim();
            value = cutString(value, maxLength);
           setNVarcharValue(index, value);
        } else
            setNullValue(index, Types.NVARCHAR);
    }

    protected void setNVarcharValue(int index, String value) throws Exception  {
        setStringValue(index, value, Types.NVARCHAR);
    }

    protected void setNVarcharValue(int index, String value, int maxLength) throws Exception {
        value = cutString(value, maxLength);
        setNVarcharValue(index, value);
    }

    protected void setStringValue(int index, String value, int type) throws Exception  {
        if (value.equals("null") || value == null || value.length() == 0)
            setNullValue(index, type);
        else
            prepStat.setString(index, value);
    }

    protected void setNullValue(int index, int type) throws Exception {
        prepStat.setNull(index, type);
    }

    protected void setIntValue(int index, JSONObject obj, String key, int type) throws Exception  {
        if (obj.has(key) && !JSONObject.NULL.equals(obj.get(key))) {
            int value = obj.getInt(key);
            if (value == 0)
                setNullValue(index, type);
            else
                prepStat.setInt(index, value);
        } else
            setNullValue(index, type);
    }

    protected void execute() throws SQLException {
        prepStat.executeUpdate();
        prepStat.close();
    }

    protected int determineNextId(String tableName) throws SQLException {
        String queryLastId = "SELECT MAX(id) FROM "+tableName+";";

        int nextId = 0;
        Statement stmt = null;
        try {
            stmt = CONN.createStatement();
            ResultSet result = stmt.executeQuery(queryLastId);
            if (result.isBeforeFirst()) {
                result.next();
                nextId = result.getInt(1) + 1;
            } else
                nextId = 1;
        } catch (SQLException e) {
            System.err.println("Could not determine last inserted ID for table "+tableName+"!");
            throw e;
        } finally {
            if (stmt != null)
                stmt.close();
        }
        return nextId;
    }

    private String cutString(String str, int maxLength) {
        if(maxLength > -1)
            str = (str.length() <= maxLength) ? str : str.substring(0, maxLength-1);
        return  str;
    }
}
