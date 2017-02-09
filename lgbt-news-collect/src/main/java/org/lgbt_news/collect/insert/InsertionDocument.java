package org.lgbt_news.collect.insert;

import org.json.JSONObject;

import java.sql.*;

/**
 * @author max
 */
public class InsertionDocument extends Insertion {

    private final String QUERY = "INSERT INTO document VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    public InsertionDocument (Connection conn) {
        super(conn);
    }

    public void insert(int id, int idNewspaper, int idQueryterm, JSONObject document) {
        try {
            prepare(id, idNewspaper, idQueryterm, document);
            execute();
            insertLeadParagraph(id, document);
        } catch (SQLException e) {
            System.err.println("Could not insert document "+document+"!");
            e.printStackTrace();
        }
    }

    private void prepare(int id, int idNewspaper, int idQueryterm, JSONObject document) throws SQLException {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, id);
        setCharValue(2, document, "_id");
        setNVarcharValue(3, document, "web_url");
        setNVarcharValue(4, document, "snippet");
        setNVarcharValue(5, document, "abstract");
        setNVarcharValue(6, document,"print_page");
        setNVarcharValue(7, document, "source");
        if (document.has("headline")) {
            setNVarcharValue(8, document.getJSONObject("headline"), "main");
            setNVarcharValue(9, document.getJSONObject("headline"),"kicker");
        }
        setNVarcharValue(10, document,"pub_date");
        setNVarcharValue(11, document, "document_type");
        if (document.has("byline")) {
            String value = document.getString("byline").substring(0, 199);
            if (value.equals("null") || value == null)
                prepStat.setNull(12, Types.NVARCHAR);
            else
                prepStat.setString(12, value);
        } else
            prepStat.setNull(12, Types.NVARCHAR);
        setNVarcharValue(13, document, "type_of_material");
        setIntValue(14, document, "word_count", Types.SMALLINT);
        prepStat.setInt(15, idNewspaper);
        prepStat.setInt(16, idQueryterm);
    }


    private void insertLeadParagraph(int id, JSONObject document) {
        if (document.has("lead_paragraph")) {
            InsertionLeadparagraph insertionLeadparagph = new InsertionLeadparagraph(CONN);
            insertionLeadparagph.insert(id, document.getString("lead_paragraph"));
        }
    }

}
