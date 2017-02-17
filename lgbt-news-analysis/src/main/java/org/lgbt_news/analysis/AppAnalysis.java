package org.lgbt_news.analysis;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.lgbt_news.analysis.sentiment.SentimentAPI;
import org.lgbt_news.analysis.sentiment.SentimentCategory;
import org.lgbt_news.analysis.sentiment.aggregation.AggregationAverage;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.utils.NytDate;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

/**
 * @author max
 */
public class AppAnalysis {

    public static void main(String[] args) {
        DatabaseAccess db = new DatabaseAccess();
        String queryterm = "transgender";
        NytDate pubYear = new NytDate.Builder().year(2017).createDate();
        int halfWindowSize = 1;
        try {
            Connection connection = db.getDbConnection();
            SentimentAPI sentimentAPI = new SentimentAPI(connection);
            sentimentAPI.init(queryterm, pubYear, halfWindowSize, new AggregationAverage());
            sentimentAPI.start();
            sentimentAPI.printSentimentAnalysis();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            db.closeDbConnection();
        }
    }
}
