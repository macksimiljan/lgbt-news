package org.lgbt_news.collect.insert;

import java.sql.*;

/**
 * @author max
 */
public class InsertionNewspaper extends  Insertion {

    private int currentId;
    private int nextId;

    private final String QUERY = "INSERT INTO newspaper VALUES (?,?);";
    private final String QUERY_CURR_ID = "SELECT id FROM newspaper WHERE name=?;";

    public InsertionNewspaper (Connection conn) {
        super(conn);
    }

    public int getIdOfCurrentNewspaper() {
        return currentId;
    }

    public void insert(String name) {
        try {
            determineCurrentId(name);
            if (currentId == nextId) {
                prepare(currentId, name);
                execute();
            }
        } catch (SQLException e) {
            System.err.println("Could not insert ("+currentId+","+name+") into newspaper table!");
            e.printStackTrace();
        }
    }

    private void prepare(int id, String name) throws SQLException {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, id);
        prepStat.setString(2, name);
    }

    private void determineCurrentId(String name) throws SQLException {
        PreparedStatement stmt = null;
        try {
            nextId = determineNextId("newspaper");

            stmt = CONN.prepareStatement(QUERY_CURR_ID);
            stmt.setString(1, name);
            ResultSet result = stmt.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                currentId = result.getInt(1);
            } else
                currentId = nextId;
        } catch (SQLException e) {
            System.err.println("Could not determine right ID for newspaper "+name+"!");
            throw e;
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

}
