package org.lgbt_news.collect.insert;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author max
 */
public class InsertionLeadparagraph extends Insertion {


    private final String QUERY = "INSERT INTO leadparagraph (id_document, text) VALUES (?,?);";


    public InsertionLeadparagraph (Connection conn) {
        super(conn);
    }

    public void insert(int id, String text) {
        try {
            if (text != null && text.length() > 0) {
                prepare(id, text);
                execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepare(int id, String text) throws Exception {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, id);
        setNVarcharValue(2, text);
    }

}
