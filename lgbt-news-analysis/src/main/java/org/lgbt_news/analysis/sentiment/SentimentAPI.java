package org.lgbt_news.analysis.sentiment;

import org.apache.log4j.Logger;
import org.lgbt_news.analysis.sentiment.aggregation.Aggregation;
import org.lgbt_news.analysis.util.ExceptionHandler;
import org.lgbt_news.analysis.util.SentimentEval;
import org.lgbt_news.analysis.util.Window;

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

    private static final Logger logger = Logger.getLogger("infoLogger");

    private String queryterm;
    private int halfWindowSize;
    private Aggregation aggregation;

    private Set<SentimentEval> evaluations;
    private double[] overallPredictions;

   public SentimentAPI(String queryterm, int halfWindowSize, Aggregation aggregation) {
        this.queryterm = queryterm;
        this.halfWindowSize = halfWindowSize;
        this.aggregation = aggregation;
        evaluations = new HashSet<>();
        overallPredictions = null;
    }

    public Set<SentimentEval> getEvaluations() {
        return evaluations;
    }


    public void start(Set<Window> contexts) {
        int count = 0;
        SentimentAnnotator annotator = new SentimentAnnotator();
        for (Window contextPerDocument : contexts) {
            if ((count % 100) == 0)
                System.out.println("Annotating contexts "+count+" to "+(count+100)+" ... ");

            evaluateContext(annotator, contextPerDocument.getSentencesAsText());
            count++;
        }
    }

    private void evaluateContext(SentimentAnnotator annotator, String contextPerDocument) {
        annotator.execute(contextPerDocument);
        List<double[]> sentencePredictions = annotator.getSentencePredictions();
        SentimentEval eval = new SentimentEval(contextPerDocument, sentencePredictions);
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
        for (SentimentEval eval : evaluations)
            allContexts.addAll(eval.getPredictions());

        overallPredictions = aggregation.aggregatePredictions(allContexts);
    }


    public void printSentimentAnalysis() {
        String agg = aggregation.getClass().getSimpleName();
        final String path = "./lgbt-news-analysis/src/main/resources/sentimentAnalysis_"+queryterm+"_"+halfWindowSize+"_"+agg+".csv";
        System.out.println("\nWriting to "+path+" ... ");

        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            w.println("id\tcontext\tpredictions\taggregatedPredictions\tcategory");
            for (SentimentEval eval : evaluations)
                w.println(eval);
        } catch (IOException e) {
            ExceptionHandler.processException(this, e, "You are here: "+System.getProperty("user.dir"));
        }
    }

}
