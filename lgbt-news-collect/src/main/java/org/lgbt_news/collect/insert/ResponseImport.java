package org.lgbt_news.collect.insert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.Iterator;

/**
 * Imports an element of the responses to a request into the database
 * by coordinately calling the insertion statements.
 *
 * @author max
 */
public class ResponseImport {

    private static int idDocument = 0;

    private final Connection CONN;
    private final String NEWSPAPER;
    private final String QUERYTERM;


    public ResponseImport(Connection conn, String newspaper, String queryterm) {
        CONN = conn;
        NEWSPAPER = newspaper;
        QUERYTERM = queryterm;
    }

    public void excecuteImport(JSONObject response) {
        idDocument++;

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

    }


}
