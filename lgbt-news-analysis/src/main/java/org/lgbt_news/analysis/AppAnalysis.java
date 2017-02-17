package org.lgbt_news.analysis;

import org.lgbt_news.analysis.sentiment.SentimentAPI;
import org.lgbt_news.analysis.sentiment.aggregation.AggregationEliteGroup;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.utils.NytDate;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author max
 */
public class AppAnalysis {

    public static void main(String[] args) {
        DatabaseAccess db = new DatabaseAccess();
        String queryterm = "transgender";
        NytDate pubYear = new NytDate.Builder().year(2017).createDate();
        int halfWindowSize = 1;
        AggregationEliteGroup aggregation = new AggregationEliteGroup(2);
        Set<Integer> indices = new HashSet<>();
        indices.add(1);
        aggregation.setIndicesOfElite(indices);
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
