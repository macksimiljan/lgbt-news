package org.lgbt_news.collect;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.insert.ResponseImport;
import org.lgbt_news.collect.request.QueryTerm;
import org.lgbt_news.collect.request.RequestNewYorkTimes;
import org.lgbt_news.collect.utils.PropertyPoint;

import java.sql.Connection;
import java.util.Iterator;

/**
 * @author max
 */
public class App {

    public static Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {
        System.out.println("---");

        // specify parameter
        String apiKey = PropertyPoint.getNytKeys().get(0);
        String queryterm = QueryTerm.HOMOSEXUAL.toString();
        String responseFile = "./lgbt-news-collect/src/main/resources/responses.txt";



        DatabaseAccess db = new DatabaseAccess();
        Connection conn = db.getDbConnection();
        for (int page = 0; page < 3; page++) {
            RequestNewYorkTimes.Builder builder = new RequestNewYorkTimes.Builder(queryterm, apiKey).page(page);
            RequestNewYorkTimes request = builder.build();
            System.out.println(request);
            String newspaper = request.getUsedApi();

            try {
                // execute request
                JSONObject jsonObject = request.getResponseAsJson();
                log.info(jsonObject);

                JSONObject meta = jsonObject.getJSONObject("response").getJSONObject("meta");
                JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");
                System.out.println("newspaper: " + newspaper + ", queryterm: " + queryterm);
                System.out.println("hits: " + meta.getInt("hits") + ", offset: " + meta.getInt("offset"));
                System.out.println(request.getLimitInformation());

                // write to database
                Iterator<Object> iteratorDocs = docs.iterator();
                while (iteratorDocs.hasNext()) {
                    JSONObject response = (JSONObject) iteratorDocs.next();
                    ResponseImport responseImport = new ResponseImport(conn, newspaper, queryterm);
                    responseImport.excecuteImport(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db.closeDbConnection();

        System.out.println("---");
    }
}
