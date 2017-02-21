package org.lgbt_news.analysis;

import org.lgbt_news.analysis.sentiment.SentimentAPI;
import org.lgbt_news.analysis.sentiment.aggregation.Aggregation;
import org.lgbt_news.analysis.sentiment.aggregation.AggregationAverage;
import org.lgbt_news.analysis.simple_topic.TopicAPI;
import org.lgbt_news.analysis.util.ExceptionHandler;
import org.lgbt_news.analysis.util.SentenceExtractor;
import org.lgbt_news.analysis.util.Window;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.utils.NytDate;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author max
 */
public class AppAnalysis {

    private static final String QUERYTERM = "transgender";
    private static final int HALF_WINDOW_SIZE = 0;
    private static final Aggregation AGGR_SENTIMENT = new AggregationAverage();
    private static final float ALPHA = 1.2f;
    private static Connection CONN;

    public static void main(String[] args) {
        DatabaseAccess db = new DatabaseAccess();
        CONN = db.getDbConnection();

        try {
            NytDate beginPubYear = new NytDate.Builder().year(2010).createDate();
            NytDate endPubYear = new NytDate.Builder().year(2017).createDate();
            Set<Window> contexts = extractContexts(beginPubYear, endPubYear);
            db.closeDbConnection();

//            SentimentAPI sentimentAPI = new SentimentAPI(QUERYTERM, HALF_WINDOW_SIZE, AGGR_SENTIMENT);
//            runSentimentAPI(sentimentAPI, contexts);

            TopicAPI topicAPI = new TopicAPI(QUERYTERM, 6);
            runTopicAPI(topicAPI, contexts);
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e);
        } finally {
            db.closeDbConnection();
        }
    }

    private static Set<Window> extractContexts(NytDate pubYear) {
        Set<Window> contexts = new HashSet<>();
        try {
            System.out.print("Extracting contexts from database for " + QUERYTERM + " in " + pubYear.getYear() + " ... ");
            SentenceExtractor extractor = new SentenceExtractor(CONN);
            contexts = extractor.getWindowContexts(QUERYTERM, pubYear, HALF_WINDOW_SIZE);
            System.out.println(" (" + contexts.size() + " contexts)");
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e, "Error while extracting contexts for year "+pubYear.getYear()+".");
        }
        return contexts;
    }

    public static Set<Window> extractContexts(NytDate beginPubYear, NytDate endPubDate) {
        Set<Window> contexts = new HashSet<>();
        try {
            System.out.print("Extracting contexts from database for " + QUERYTERM + " from " + beginPubYear.getYear() + " to " + endPubDate.getYear());
            SentenceExtractor extractor = new SentenceExtractor(CONN);
            contexts = extractor.getWindowContexts(QUERYTERM, beginPubYear, endPubDate, HALF_WINDOW_SIZE);
            System.out.println(" (" + contexts.size() + " contexts)");
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e, "Error while extracting contexts from year "+beginPubYear.getYear()+" to "+endPubDate.getYear()+".");
        }
        return contexts;
    }

    private static void runSentimentAPI(SentimentAPI sentimentAPI, Set<Window> contexts) {
        try {
            System.out.println("Running sentiment API ... ");
            long timeStart = System.currentTimeMillis();
            sentimentAPI.start(contexts);
            System.out.println(" >> "+(System.currentTimeMillis()-timeStart)/1000+"s");
            sentimentAPI.printSentimentAnalysis();
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e, "Error while running sentiment API.");
        }
    }

    private static void runTopicAPI(TopicAPI topicAPI, Set<Window> contexts) {
        try {
            System.out.println("Running topic API ... ");
            long timeStart = System.currentTimeMillis();
            topicAPI.start(contexts);
            System.out.println(" >> "+(System.currentTimeMillis()-timeStart)/1000+"s");
            topicAPI.printTopicAnalysis();
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e, "Error while running topic API.");
        }
    }
}
