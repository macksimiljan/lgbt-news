package org.lgbt_news.analysis.sentiment.aggregation;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Summarizes sentiment categories by majority vote.
 * When two or more categories have the maximum number of votes, the more negative one is returned.
 * The probability is the inverse of the number of maxima.
 *
 * @author max
 */
public class AggregationMajorityVote implements Aggregation {

    private double probability;

    public AggregationMajorityVote() {
        probability = -1;
    }

    @Override
    public SentimentCategory aggregateCategories(List<SentimentCategory> categories) {
        Map<SentimentCategory, Integer> map = new HashMap<>();
        for (SentimentCategory c : categories) {
            if (map.containsKey(c)) {
                int val = map.get(c);
                val++;
                map.put(c, val);
            } else
                map.put(c, 1);
        }

        SentimentCategory winner = null;
        int maxCount = -1;
        int noOfMaxima = 1;
        for (Map.Entry<SentimentCategory, Integer> entry : map.entrySet()) {
            int val = entry.getValue();
            if (val > maxCount) {
                maxCount = val;
                winner = entry.getKey();
            } else if (val == maxCount) {
                winner = (entry.getKey().categoryToId() < winner.categoryToId()) ? entry.getKey() : winner;
                noOfMaxima++;
            }
        }

        probability = 1.0 / noOfMaxima;

        return winner;
    }

    @Override
    public SentimentCategory aggregatePredictions(List<double[]> predictions) {
        List<SentimentCategory> categories = new ArrayList<>();
        for (double[] p : predictions)
            categories.add(SentimentCategory.getCategoryFromPredictions(p));

        return aggregateCategories(categories);
    }



    @Override
    public double getProbability() {
        return probability;
    }
}
