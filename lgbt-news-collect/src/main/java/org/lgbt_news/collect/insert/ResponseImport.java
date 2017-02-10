package org.lgbt_news.collect.insert;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Imports an element of the responses to a request into the database
 * by coordinately calling the insertion statements.
 *
 * @author max
 */
public class ResponseImport {

    static final Logger logger = Logger.getLogger("infoLogger");

    private static int idDocument = 1;

    private final Connection CONN;
    private final String NEWSPAPER;
    private final String QUERYTERM;


    public ResponseImport(Connection conn, String newspaper, String queryterm) {
        CONN = conn;
        NEWSPAPER = newspaper;
        QUERYTERM = queryterm;
    }

    public static void initializeIdDocument(Connection conn) {
        String query = "SELECT MAX(id) FROM document;";
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            if (result.isBeforeFirst()) {
                result.next();
                int highestId = result.getInt(1);
                idDocument = highestId +1 ;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void excecuteImport(JSONObject response) {
        try {
            InsertionNewspaper insertionNewspaper = new InsertionNewspaper(CONN);
            insertionNewspaper.insert(NEWSPAPER);
            int idNewspaper = insertionNewspaper.getIdOfCurrentNewspaper();

            InsertionQueryterm insertionQueryterm = new InsertionQueryterm(CONN);
            insertionQueryterm.insert(QUERYTERM);
            int idQueryterm = insertionQueryterm.getIdOfCurrentQueryterm();

            InsertionDocument insertionDocument = new InsertionDocument(CONN);
            insertionDocument.insert(idDocument, idNewspaper, idQueryterm, response);

            InsertionKeyword insertionKeyword = new InsertionKeyword(CONN);
            InsertionDocumentKeyword insertionDocumentKeyword = new InsertionDocumentKeyword(CONN);
            JSONArray keywords = response.getJSONArray("keywords");
            Iterator<Object> iterator = keywords.iterator();
            while (iterator.hasNext()) {
                JSONObject keyword = (JSONObject) iterator.next();
                insertionKeyword.insert(keyword);
                int idKeyword = insertionKeyword.getIdOfCurrentKeyword();
                insertionDocumentKeyword.insert(idDocument, idKeyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("class:ResponseImport\tresponse:"+response+"\tmessage:"+e.getMessage());
        } finally {
            idDocument++;
        }
    }


}
