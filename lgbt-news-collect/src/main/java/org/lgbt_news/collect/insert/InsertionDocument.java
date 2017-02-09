package org.lgbt_news.collect.insert;

import org.json.JSONException;
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
        } catch (Exception e ) {
            System.err.println("Could not insert document "+document+"!");
            e.printStackTrace();
        }
    }

    private void prepare(int id, int idNewspaper, int idQueryterm, JSONObject document) throws Exception {
        prepStat = CONN.prepareStatement(QUERY);
        prepStat.setInt(1, id);
        setCharValue(2, document, "_id");
        setNVarcharValue(3, document, "web_url", 150);
        setNVarcharValue(4, document, "snippet", 250);
        setNVarcharValue(5, document, "abstract", 1000);
        setNVarcharValue(6, document,"print_page", 20);
        setNVarcharValue(7, document, "source", 100);
        if (document.has("headline")  && !JSONObject.NULL.equals(document.get("headline"))) {
            setNVarcharValue(8, document.getJSONObject("headline"), "main", 100);
            setNVarcharValue(9, document.getJSONObject("headline"),"kicker", 100);
        }
        setNVarcharValue(10, document,"pub_date", 20);
        setNVarcharValue(11, document, "document_type", 20);
        if (document.has("byline") && !JSONObject.NULL.equals(document.get("byline"))) {
            String value = document.getJSONObject("byline").toString();
            setNVarcharValue(12, value, 200);
        } else
            setNullValue(12, Types.NVARCHAR);
        setNVarcharValue(13, document, "type_of_material");
        setIntValue(14, document, "word_count", Types.SMALLINT);
        prepStat.setInt(15, idNewspaper);
        prepStat.setInt(16, idQueryterm);
    }


    private void insertLeadParagraph(int id, JSONObject document) {
        if (document.has("lead_paragraph") && !JSONObject.NULL.equals(document.get("lead_paragraph"))) {
            InsertionLeadparagraph insertionLeadparagph = new InsertionLeadparagraph(CONN);
            insertionLeadparagph.insert(id, document.getString("lead_paragraph"));
        }
    }

}
