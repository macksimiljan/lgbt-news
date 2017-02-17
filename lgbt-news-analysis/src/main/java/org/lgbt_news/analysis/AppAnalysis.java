package org.lgbt_news.analysis;

import org.lgbt_news.analysis.sentiment.SentimentAPI;
import org.lgbt_news.analysis.sentiment.aggregation.Aggregation;
import org.lgbt_news.analysis.sentiment.aggregation.AggregationAverage;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.utils.NytDate;

import java.sql.Connection;

/**
 * @author max
 */
public class AppAnalysis {

    public static void main(String[] args) {
        DatabaseAccess db = new DatabaseAccess();
        String queryterm = "transgender";
        NytDate pubYear = new NytDate.Builder().year(2017).createDate();
        int halfWindowSize = 0;
        Aggregation aggregation = new AggregationAverage();
        try {
            Connection connection = db.getDbConnection();
            SentimentAPI sentimentAPI = new SentimentAPI(connection);
            sentimentAPI.init(queryterm, pubYear, halfWindowSize, aggregation);
            sentimentAPI.start();
            sentimentAPI.printSentimentAnalysis();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            db.closeDbConnection();
        }
    }
}
