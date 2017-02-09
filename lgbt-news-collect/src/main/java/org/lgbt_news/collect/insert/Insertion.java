package org.lgbt_news.collect.insert;

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

    protected void setCharValue(int index, JSONObject obj, String key) throws SQLException {
        if (obj.has(key)) {
            String value = obj.getString(key).trim();
            setStringValue(index, value, Types.CHAR);
        } else
            setNullValue(index, Types.CHAR);
    }

    protected void setNVarcharValue(int index, JSONObject obj, String key) throws SQLException {
        if (obj.has(key)) {
            String value = obj.getString(key).trim();
            setNVarcharValue(index, value);
        } else
            setNullValue(index, Types.NVARCHAR);
    }

    protected void setNVarcharValue(int index, String value) throws SQLException {
        setStringValue(index, value, Types.NVARCHAR);
    }

    protected void setStringValue(int index, String value, int type) throws SQLException {
        if (value.equals("null") || value == null || value.length() == 0)
            setNullValue(index, type);
        else
            prepStat.setString(index, value);
    }

    protected void setNullValue(int index, int type) throws SQLException {
        prepStat.setNull(index, type);
    }

    protected void setIntValue(int index, JSONObject obj, String key, int type) throws SQLException {
        if (obj.has(key)) {
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
}
