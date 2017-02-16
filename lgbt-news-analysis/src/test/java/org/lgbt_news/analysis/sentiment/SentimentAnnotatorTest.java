package org.lgbt_news.analysis.sentiment;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class SentimentAnnotatorTest {
    @Test
    public void text_execute() throws Exception {
        SentimentAnnotator annotator = new SentimentAnnotator();

        String text = "The movie was great and enjoyable. I love that film. Yesterday I saw an ugly and smelly cat.";

        annotator.execute(text);
        List<SentimentCategory> categories = annotator.getCategoriesPerSentence();
        List<double[]> predictions = annotator.getPredictionsPerSentence();
        SentimentCategory[] expectedCategories = {SentimentCategory.VERY_POS, SentimentCategory.POS, SentimentCategory.NEG};
        double[] expectedProbabilities = {0.76076, 0.639197, 0.50958};

        for (int i = 0; i < categories.size(); i++) {
            SentimentCategory c = categories.get(i);
            double probability = predictions.get(i)[c.categoryToId()];
            assertEquals(expectedCategories[i], c);
            assertEquals(expectedProbabilities[i], probability, 0.001);
        }

    }

}