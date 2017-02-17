package org.lgbt_news.analysis.sentiment.aggregation;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Specific elements of the list of elements/ predictions are seen as elite.
 * They have more strength in the aggregation.
 * E.g.: {POS, NEG, NEG} with POS as elite yields POS due to (elite) majority vote
 * with stength of 3 (or greater): 3 votes for POS, 2 votes for NEG.
 *
 * @author max
 */
public class AggregationEliteGroup implements Aggregation {

    private final int STRENGTH_OF_ELITE;

    private Set<Integer> indicesOfElite;
    private AggregationMajorityVote majorityVote;

    public AggregationEliteGroup(int strengthOfElite) {
        STRENGTH_OF_ELITE = (strengthOfElite < 2) ? 2 : strengthOfElite;
        indicesOfElite = new HashSet<>();
        majorityVote = new AggregationMajorityVote();
    }

    public void setIndicesOfElite(Set<Integer> indicesOfElite) {
        this.indicesOfElite = indicesOfElite;
    }

    @Override
    public SentimentCategory aggregateCategories(List<SentimentCategory> categories) {
        for (int index : indicesOfElite) {
            SentimentCategory eliteCategory = categories.get(index);
            for (int i = 0; i < STRENGTH_OF_ELITE; i++) {
                categories.add(eliteCategory);
            }
        }

        return majorityVote.aggregateCategories(categories);
    }

    @Override
    public SentimentCategory aggregatePredictions(List<double[]> predictions) {
        for (int index : indicesOfElite) {
            double[] elitePrediction = predictions.get(index);
            for (int i = 0; i < STRENGTH_OF_ELITE; i++) {
                predictions.add(elitePrediction);
            }
        }

        return majorityVote.aggregatePredictions(predictions);
    }

    @Override
    public double getProbability() {
        return majorityVote.getProbability();
    }
}
