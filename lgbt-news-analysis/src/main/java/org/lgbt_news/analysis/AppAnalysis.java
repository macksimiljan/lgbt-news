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

    private static final String[] QUERY_TERMS = {"transgender","transsexual","homosexual","lesbian","gay community","gay","bisexual","queer"};
    private static final int HALF_WINDOW_SIZE = 1;
    private static final Aggregation AGGR_SENTIMENT = new AggregationAverage();
    private static  int MAX_DISTANCE_COOCCURRENCE = 14;
    private static Connection CONN;


    public static void main(String[] args) {
        DatabaseAccess db = new DatabaseAccess();
        CONN = db.getDbConnection();

        try {
            for (int beginYear = 1850; beginYear < 2020; beginYear += 10)
                for (String queryTerm : QUERY_TERMS)
                    processQueryTermInInterval(queryTerm, beginYear, beginYear+9);
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e);
        } finally {
            db.closeDbConnection();
        }
    }

    private static void processQueryTermInInterval(String queryTerm, int beginYear, int endYear) {
        System.out.println();
        NytDate beginPubYear = new NytDate.Builder().year(beginYear).createDate();
        NytDate endPubYear = new NytDate.Builder().year(endYear).createDate();
        Set<Window> contexts = extractContexts(queryTerm, beginPubYear, endPubYear);

        SentimentAPI sentimentAPI = new SentimentAPI(queryTerm, AGGR_SENTIMENT);
        runSentimentAPI(sentimentAPI, contexts);

        TopicAPI topicAPI = new TopicAPI(queryTerm, MAX_DISTANCE_COOCCURRENCE);
        topicAPI.setPath("./lgbt-news-analysis/src/main/resources/topics/" +
                "topics_"+queryTerm+beginYear+"to"+endYear+"_"+MAX_DISTANCE_COOCCURRENCE+".csv");
        runTopicAPI(topicAPI, contexts);
    }

    private static Set<Window> extractContexts(String queryTerm, NytDate beginPubYear, NytDate endPubDate) {
        Set<Window> contexts = new HashSet<>();
        try {
            System.out.print("Extracting contexts from database for " + queryTerm + " from " + beginPubYear.getYear() + " to " + endPubDate.getYear());
            SentenceExtractor extractor = new SentenceExtractor(CONN);
            contexts = extractor.getWindowContexts(queryTerm, beginPubYear, endPubDate, HALF_WINDOW_SIZE);
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
            sentimentAPI.printSentimentAnalysis(HALF_WINDOW_SIZE);
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
            topicAPI.printTopicAnalysis(true, true);
        } catch (Exception e) {
            ExceptionHandler.processException(new AppAnalysis(), e, "Error while running topic API.");
        }
    }
}
