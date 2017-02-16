package org.lgbt_news.analysis.sentiment.aggregation;

import org.junit.Test;
import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author max
 */
public class AggregationMajorityVoteTest {
    @Test
    public void test_aggregateCategories() throws Exception {
        AggregationMajorityVote aggregation = new AggregationMajorityVote();

        List<SentimentCategory> categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.POS);
        assertEquals(SentimentCategory.NEG, aggregation.aggregateCategories(categories));

        categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.VERY_NEG, SentimentCategory.POS);
        assertEquals(SentimentCategory.NEG, aggregation.aggregateCategories(categories));

        categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.POS, SentimentCategory.POS);
        assertEquals(SentimentCategory.NEG, aggregation.aggregateCategories(categories));

        categories = Arrays.asList(
                SentimentCategory.POS, SentimentCategory.POS, SentimentCategory.NEG, SentimentCategory.NEG);
        assertEquals(SentimentCategory.NEG, aggregation.aggregateCategories(categories));
    }

    @Test
    public void test_aggregatePredictions() throws Exception {
        AggregationMajorityVote aggregation = new AggregationMajorityVote();

        double[] probabilities1 = {0.08, 0.5, 0.01, 0.01, 0.4};
        double[] probabilities2 = {0.01, 0.4, 0.1, 0.2, 0.29};
        double[] probabilities3 = {0.24, 0.3, 0.15, 0.05, 0.26};
        double[] probabilities4 = {0.2, 0.35, 0.5, 0.2, 0.4};
        List<double[]> predictions = new ArrayList<>();
        predictions.add(probabilities1);
        predictions.add(probabilities2);
        predictions.add(probabilities3);
        predictions.add(probabilities4);
        assertEquals(SentimentCategory.NEG, aggregation.aggregatePredictions(predictions));

        double[] probabilities5 = {0.08, 0.5, 0.01, 0.01, 0.4};
        double[] probabilities6 = {0.01, 0.4, 0.1, 0.2, 0.29};
        double[] probabilities7 = {0.3, 0.24, 0.15, 0.05, 0.26};
        double[] probabilities8 = {0.01, 0.001, 0.009, 0.02, 0.96};
        predictions = new ArrayList<>();
        predictions.add(probabilities5);
        predictions.add(probabilities6);
        predictions.add(probabilities7);
        predictions.add(probabilities8);
        assertEquals(SentimentCategory.NEG, aggregation.aggregatePredictions(predictions));

    }

    @Test
    public void test_getProbability() throws Exception {
        AggregationMajorityVote aggregation = new AggregationMajorityVote();

        List<SentimentCategory> categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.POS);
        aggregation.aggregateCategories(categories);
        assertEquals(1.0, aggregation.getProbability(), 0.000001);

        categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.POS, SentimentCategory.POS);
        aggregation.aggregateCategories(categories);
        assertEquals(0.5, aggregation.getProbability(), 0.000001);

    }

}