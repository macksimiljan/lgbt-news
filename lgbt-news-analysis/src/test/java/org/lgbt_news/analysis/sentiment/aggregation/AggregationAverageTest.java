package org.lgbt_news.analysis.sentiment.aggregation;

import org.junit.Test;
import org.lgbt_news.analysis.sentiment.SentimentCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class AggregationAverageTest {
    @Test
    public void test_aggregateCategories() throws Exception {
        AggregationAverage aggregationAverage = new AggregationAverage();

        List<SentimentCategory> categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.POS);
        assertEquals(SentimentCategory.NEUTRAL, aggregationAverage.aggregateCategories(categories));

        categories = Arrays.asList(
                SentimentCategory.NEG, SentimentCategory.NEG, SentimentCategory.VERY_NEG, SentimentCategory.POS);
        assertEquals(SentimentCategory.NEG, aggregationAverage.aggregateCategories(categories));

    }

    @Test
    public void test_aggregatePredictions() throws Exception {
        AggregationAverage aggregationAverage = new AggregationAverage();

        double[] probabilities1 = {0.08, 0.5, 0.01, 0.01, 0.4};
        double[] probabilities2 = {0.01, 0.4, 0.1, 0.2, 0.29};
        double[] probabilities3 = {0.24, 0.3, 0.15, 0.05, 0.26};
        double[] probabilities4 = {0.2, 0.35, 0.5, 0.2, 0.4};
        List<double[]> predictions = new ArrayList<>();
        predictions.add(probabilities1);
        predictions.add(probabilities2);
        predictions.add(probabilities3);
        predictions.add(probabilities4);
        assertEquals(SentimentCategory.NEG, aggregationAverage.aggregatePredictions(predictions));

        double[] probabilities5 = {0.08, 0.5, 0.01, 0.01, 0.4};
        double[] probabilities6 = {0.01, 0.4, 0.1, 0.2, 0.29};
        double[] probabilities7 = {0.3, 0.24, 0.15, 0.05, 0.26};
        double[] probabilities8 = {0.01, 0.001, 0.009, 0.02, 0.96};
        predictions = new ArrayList<>();
        predictions.add(probabilities5);
        predictions.add(probabilities6);
        predictions.add(probabilities7);
        predictions.add(probabilities8);
        assertEquals(SentimentCategory.VERY_POS, aggregationAverage.aggregatePredictions(predictions));
    }

}