package org.lgbt_news.analysis.sentiment.aggregation;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.List;

/**
 * @author max
 */
public class AggregationAverage implements  Aggregation {

    private double probability;

    public AggregationAverage() {
        resetProbability();
    }

    @Override
    public SentimentCategory aggregateCategories(List<SentimentCategory> categories) {
        resetProbability();
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
    public SentimentCategory aggregatePredictions(List<double[]> predictions) {
        double[] probabilities = new double[SentimentCategory.values().length];
        for (double[] prediction: predictions) {
            for (int i = 0; i < prediction.length; i++)
               probabilities[i] += prediction[i];
        }

        SentimentCategory aggrCategory = null;
        double maxProbability = -1;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                aggrCategory = SentimentCategory.idToCategory(i);
            }
        }
        probability = maxProbability / predictions.size();

        return aggrCategory;
    }

    @Override
    public double getProbability() {
        return probability;
    }

    private void resetProbability() {
        probability = -1;
    }
}
