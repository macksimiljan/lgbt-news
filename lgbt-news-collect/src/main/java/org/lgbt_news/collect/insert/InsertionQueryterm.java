package org.lgbt_news.collect.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author max
 */
public class InsertionQueryterm extends Insertion {

    private int currentId;
    private int nextId;

    private final String QUERY = "INSERT INTO queryterm VALUES (?,?);";
    private final String QUERY_CURR_ID = "SELECT id FROM queryterm WHERE term=?;";

    public InsertionQueryterm (Connection conn) {
        super(conn);
    }

    public int getIdOfCurrentQueryterm() {
        return currentId;
    }

    public void insert(String term) {
        try {
            determineCurrentId(term);
            if (currentId == nextId) {
                prepare(currentId, term);
                execute();
            }
        } catch (SQLException e) {
            System.err.println("Could not insert ("+currentId+","+term+") into queryterm table!");
            e.printStackTrace();
        }
    }

    private void determineCurrentId(String term) throws SQLException {
        PreparedStatement stmt = null;
        try {
            nextId = determineNextId("queryterm");

            stmt = CONN.prepareStatement(QUERY_CURR_ID);
            stmt.setString(1, term);
            ResultSet result = stmt.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                currentId = result.getInt(1);
            } else
                currentId = nextId;
        } catch (SQLException e) {
            System.err.println("Could not determine right ID for queryterm "+term+"!");
            throw e;
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    private void prepare(int id, String term) throws SQLException {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, id);
        prepStat.setString(2, term);
    }

}
