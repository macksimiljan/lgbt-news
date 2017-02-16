package org.lgbt_news.analysis.sentiment.aggregation;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.List;

/**
 * @author max
 */
public interface Aggregation {

    SentimentCategory aggregateCategories(List<SentimentCategory> categories);
    SentimentCategory aggregatePredictions(List<double[]> predictions);
    double getProbability();

}
