package org.lgbt_news.collect.insert;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author max
 */
public class InsertionDocumentKeyword extends  Insertion{

    static final Logger logger = Logger.getLogger("infoLogger");

    private final String QUERY = "INSERT INTO document_keyword VALUES (?,?);";

    public InsertionDocumentKeyword (Connection conn) {
        super(conn);
    }

    public void insert(int idDocument, int idKeyword) {
        try {
            prepare(idDocument, idKeyword);
            execute();
        } catch (SQLException e) {
            logger.error("Could not insert ("+idDocument+","+idKeyword+") " +
                    "into document_keyword table: "+e.getMessage()+"!");
        }
    }

    private void prepare(int idDocument, int idKeyword) throws SQLException {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, idDocument);
        prepStat.setInt(2, idKeyword);
    }
}
