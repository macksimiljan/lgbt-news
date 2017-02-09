package org.lgbt_news.collect.insert;

import org.json.JSONObject;

import java.sql.*;

/**
 * @author max
 */
public class InsertionKeyword extends Insertion {

    private int currentId;
    private int nextId;

    private final String QUERY = "INSERT INTO keyword VALUES (?,?,?);";
    private final String QUERY_CURR_ID = "SELECT id FROM keyword WHERE name=? AND value=?;";

    public InsertionKeyword(Connection conn) {
        super(conn);
    }

    public int getIdOfCurrentKeyword() {
        return currentId;
    }

    public void insert(JSONObject keyword) {
        try {
            determineCurrentId(keyword);
            if (currentId == nextId) {
                prepare(currentId, keyword);
                execute();
            }
        } catch (Exception e) {
            System.err.println("Could not insert keyword "+keyword+"!");
            e.printStackTrace();
        }
    }

    private void determineCurrentId(JSONObject keyword) throws SQLException {
        PreparedStatement stmt = null;
        try {
            nextId = determineNextId("keyword");

            stmt = CONN.prepareStatement(QUERY_CURR_ID);
            stmt.setString(1, keyword.getString("name"));
            stmt.setString(2, keyword.getString("value"));
            ResultSet result = stmt.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                currentId = result.getInt(1);
            } else
                currentId = nextId;
        } catch (SQLException e) {
            System.err.println("Could not determine right ID for keyword "+keyword+"!");
            throw e;
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    private void prepare(int id, JSONObject keyword) throws Exception {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, id);
        setNVarcharValue(2, keyword.getString("name"), 50);
        setNVarcharValue(3, keyword.getString("value"), 100);
    }


}
