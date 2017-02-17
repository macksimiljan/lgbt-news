package org.lgbt_news.analysis.sentiment.aggregation;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.List;

/**
 * @author max
 */
public class AggregationAverage implements  Aggregation {

    @Override
    public SentimentCategory aggregateCategories(List<SentimentCategory> categories) {
        int sum = 0;
        for (SentimentCategory c : categories) {
            int id = c.categoryToId();
            sum += id;
        }
        double avg = 1.0*sum / categories.size();
        int avgId = (int) Math.round(avg);
        return SentimentCategory.idToCategory(avgId);
    }

    @Override
    public double[] aggregatePredictions(List<double[]> predictions) {
        double[] probabilities = new double[SentimentCategory.values().length];
        for (double[] prediction: predictions) {
            for (int i = 0; i < prediction.length; i++)
               probabilities[i] += prediction[i];
        }

        for (int i = 0; i < probabilities.length; i++)
            probabilities[i] /= predictions.size();

        return probabilities;
    }

}
