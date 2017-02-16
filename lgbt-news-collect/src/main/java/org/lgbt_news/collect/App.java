package org.lgbt_news.collect;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.insert.ResponseImport;
import org.lgbt_news.collect.request.QueryTerm;
import org.lgbt_news.collect.request.RequestNewYorkTimes;
import org.lgbt_news.collect.utils.NytDate;
import org.lgbt_news.collect.utils.PropertyPoint;

import java.sql.Connection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author max
 */
public class App {

    private static final Logger infoLogger = Logger.getLogger("infoLogger");
    private static final Logger requestLogger = Logger.getLogger("requestLogger");

    private static Connection conn;
    private static String newspaper;
    private static String apiKey;
    private static String queryterm;

    private static int hits = Integer.MAX_VALUE;
    private static boolean isFirstRequest;


    public static void main(String[] args) {
        System.out.println("---");

        DatabaseAccess db = new DatabaseAccess();
        conn = db.getDbConnection();
        newspaper = "New York Times";
        apiKey = PropertyPoint.getNytKeys().get(0);
        queryterm = QueryTerm.GAY_COMMUNITY.toString();
        infoLogger.info("newspaper:" + newspaper + ",queryterm:" + queryterm);
        int interval = 2;

        ResponseImport.initializeIdDocument(conn);
        for (int startYear = 1972; startYear < 2018; startYear += interval) {
            isFirstRequest = true;

            int endYear = startYear + (interval - 1);
            NytDate beginDate = new NytDate.Builder().year(startYear).createDate();
            NytDate endDate = new NytDate.Builder().year(endYear).month(12).day(31).createDate();

            int page = 0;
            hits = Integer.MAX_VALUE;
            while (page <= 120 && hits > 0) {
                infoLogger.info("querying for page:" + page + ",hits left:" + hits);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    collectNYT(page, beginDate, endDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    infoLogger.error("class:App\tmessage:" + e.getMessage());
                }
                page++;
                hits -= 10;
            }
        }

        db.closeDbConnection();

        System.out.println("---");
    }

    private static void collectNYT(int page, NytDate beginDate, NytDate endDate) {
        RequestNewYorkTimes request = new RequestNewYorkTimes.Builder(queryterm, apiKey)
                .page(page).beginDate(beginDate.toString()).endDate(endDate.toString()).build();
        System.out.println(request);
        infoLogger.info("request:"+request);

        try {
            // execute request
            JSONObject jsonObject = request.getResponseAsJson();
            requestLogger.info(jsonObject);

            JSONObject meta = jsonObject.getJSONObject("response").getJSONObject("meta");
            JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");
            if (isFirstRequest) {
                hits = meta.getInt("hits");
                infoLogger.info("actual hits:" + hits + ",limit:" + request.getLimitInformation());
                isFirstRequest = false;
            }

            // write to database
            Iterator<Object> iteratorDocs = docs.iterator();
            while (iteratorDocs.hasNext()) {
                JSONObject response = (JSONObject) iteratorDocs.next();
                ResponseImport responseImport = new ResponseImport(conn, newspaper, queryterm);
                responseImport.excecuteImport(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            infoLogger.error("class:App\trequest:"+request+"\tmessage:"+e.getMessage());
        }
    }
}
