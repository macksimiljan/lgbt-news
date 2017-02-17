package org.lgbt_news.analysis.sentiment;

import org.apache.log4j.Logger;
import org.lgbt_news.analysis.sentiment.aggregation.Aggregation;
import org.lgbt_news.analysis.util.SentenceEval;
import org.lgbt_news.analysis.util.SentenceExtractor;
import org.lgbt_news.collect.utils.NytDate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author max
 */
public class SentimentAPI {

    static final Logger logger = Logger.getLogger("infoLogger");

    private final Connection CONN;

    private String queryterm;
    private NytDate pubYear;
    private int halfWindowSize;
    private Aggregation aggregation;

    private int countContext;
    private Set<SentenceEval> evaluations;
    private double[] overallPredictions;

    public SentimentAPI(Connection connection) {
        CONN = connection;
        queryterm = "";
        halfWindowSize = 0;
        evaluations = new HashSet<>();
    }

    public int getCountContext() {
        return countContext;
    }

    public Set<SentenceEval> getEvaluations() {
        return evaluations;
    }

    public void init(String queryterm, NytDate pubYear, int halfWindowSize, Aggregation aggregation) {
        System.out.println("Initializing sentiment analysis ... ");
        this.queryterm = queryterm;
        this.pubYear = pubYear;
        this.halfWindowSize = halfWindowSize;
        this.aggregation = aggregation;
        countContext = 0;
        evaluations = new HashSet<>();
        overallPredictions = null;
    }

    public void start() throws IllegalAccessException {
        checkInit();

        System.out.print("Extracting contexts from database for "+queryterm+" in "+pubYear.getYear()+" ... ");
        SentenceExtractor extractor = new SentenceExtractor(CONN);
        Set<String> contexts = extractor.getWindowContexts(queryterm, pubYear, halfWindowSize);
        countContext = contexts.size();
        System.out.println(" ("+contexts.size()+" contexts)");

        int count = 0;
        SentimentAnnotator annotator = new SentimentAnnotator();
        for (String contextPerDocument : contexts) {
            if ((count % 100) == 0)
                System.out.println("Annotating contexts "+count+" to "+(count+100)+" ... ");

            evaluateContext(annotator, contextPerDocument);
            count++;
        }
    }

    private void checkInit() throws IllegalAccessException {
        if (queryterm.length() < 1)
            throw new IllegalAccessException("You have to initialize the API first!");
    }

    private void evaluateContext(SentimentAnnotator annotator, String contextPerDocument) {
        annotator.execute(contextPerDocument);
        List<double[]> sentencePredictions = annotator.getSentencePredictions();
        SentenceEval eval = new SentenceEval(contextPerDocument, sentencePredictions);
        eval.setAggregatedPredictions(aggregation);
        evaluations.add(eval);
    }

    public double[] getOverallPredictions() {
        if (overallPredictions == null)
            aggregateContexts();

        return overallPredictions;
    }

    private void aggregateContexts() {
        List<double[]> allContexts = new ArrayList<>();
        for (SentenceEval eval : evaluations)
            allContexts.addAll(eval.getPredictions());

        overallPredictions = aggregation.aggregatePredictions(allContexts);
    }


    public void printSentimentAnalysis() {
        String agg = aggregation.getClass().getSimpleName();
        final String path = "./lgbt-news-analysis/src/main/resources/sentimentAnalysis_"+queryterm+"_"+halfWindowSize+"_"+agg+".csv";
        System.out.println("\nWriting to "+path+" ... ");

        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            w.println("id\tcontext\tpredictions\taggregatedPredictions\tcategory");
            for (SentenceEval eval : evaluations)
                w.println(eval);
        } catch (IOException e) {
            System.out.println("You are here: "+System.getProperty("user.dir"));
            e.printStackTrace();
            logger.error(this.getClass().getName()+"\t"+e.getMessage());
        }
    }

}
