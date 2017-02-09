package org.lgbt_news.collect.insert;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author max
 */
public class InsertionDocumentKeyword extends  Insertion{

    private final String QUERY = "INSERT INTO document_keyword VALUES (?,?);";

    public InsertionDocumentKeyword (Connection conn) {
        super(conn);
    }

    public void insert(int idDocument, int idKeyword) {
        try {
            prepare(idDocument, idKeyword);
            execute();
        } catch (SQLException e) {
            System.err.println("Could not insert ("+idDocument+","+idKeyword+") into document_keyword table!");
            e.printStackTrace();
        }
    }

    private void prepare(int idDocument, int idKeyword) throws SQLException {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, idDocument);
        prepStat.setInt(2, idKeyword);
    }
}
