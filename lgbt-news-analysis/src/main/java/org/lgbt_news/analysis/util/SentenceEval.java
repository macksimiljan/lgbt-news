package org.lgbt_news.analysis.util;

import org.lgbt_news.analysis.sentiment.SentimentCategory;
import org.lgbt_news.analysis.sentiment.aggregation.Aggregation;
import org.lgbt_news.analysis.sentiment.filter.Filter;
import org.lgbt_news.analysis.sentiment.filter.FilterThreshold;

import java.util.List;

/**
 * @author max
 */
public class SentenceEval {

    private static int counter = 1;

    private final int id;
    private final String window;
    private final List<double[]> predictions;
    private double[] aggregatedPredictions;

    public SentenceEval(String window, List<double[]> predictions) {
        id = counter++;
        this.window = window;
        this.predictions = predictions;
        aggregatedPredictions = null;
    }

    public String getWindow() {
        return window;
    }

    public List<double[]> getPredictions() {
        return predictions;
    }

    public SentimentCategory getCategory() {
        return SentimentCategory.getCategoryFromPredictions(aggregatedPredictions);
    }

    public SentimentCategory getCategory(Filter filter) {
        return filter.apply(aggregatedPredictions);
    }

    public double[] getAggregatedPredictions(Aggregation aggregation) {
        if (aggregatedPredictions == null && predictions.size() == 1)
            aggregatedPredictions = predictions.get(0);
        else if (aggregatedPredictions == null)
            aggregatedPredictions = aggregation.aggregatePredictions(predictions);

        return aggregatedPredictions;
    }

    public void setAggregatedPredictions(Aggregation aggregation) {
        if (predictions.size() == 1)
            aggregatedPredictions = predictions.get(0);
        else
            aggregatedPredictions = aggregation.aggregatePredictions(predictions);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        String predictionsString = "[";
        for (double[] p : predictions)
            predictionsString += predictionsToString(p);
        predictionsString += "]";

        String aggregatedPredictString = predictionsToString(aggregatedPredictions);

        Filter filter = new FilterThreshold(0.25);
        return id+"\t"+window+"\t"+predictionsString+"\t"+aggregatedPredictString+"\t"+getCategory(filter);
    }

    private String predictionsToString(double[] p) {
        return "["+round(p[0])+","+round(p[1])+","+round(p[2])+","+round(p[3])+","+round(p[4])+"]";
    }
    private double round(double d) {
        return Math.round(d*1000) / 1000.0;
    }
}
