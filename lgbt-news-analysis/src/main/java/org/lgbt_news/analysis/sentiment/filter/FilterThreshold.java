package org.lgbt_news.analysis.sentiment.filter;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.Arrays;

/**
 * @author max
 */
public class FilterThreshold implements Filter{

    private final double THRESHOLD;

    public FilterThreshold(double threshold) {
        THRESHOLD = threshold;
    }

    public SentimentCategory apply(double[] predictions) {
        SentimentCategory category = SentimentCategory.NEUTRAL;
        try {
            double max = Arrays.stream(predictions).max().getAsDouble();
            if (max >= THRESHOLD)
                category = SentimentCategory.getCategoryFromPredictions(predictions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return category;
    }
}
