package org.lgbt_news.analysis.sentiment;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class SentimentAnnotatorTest {
    @Test
    public void text_execute() throws Exception {
        SentimentAnnotator annotator = new SentimentAnnotator();

        String text = "The movie was great and enjoyable. I love that film. Yesterday I saw an ugly and smelly cat." +
                "Being a transsexual means facing stares every day.";

//        String text = "After many years of divisiveness, the Boy Scouts of America have opened their ranks to gay and transgender boys. " +
//                "A transgender woman celebrates the decision to include transgender boys in the Scouts.";

        annotator.execute(text);
        List<SentimentCategory> categories = annotator.getSentenceCategories();
        List<double[]> predictions = annotator.getSentencePredictions();
        SentimentCategory[] expectedCategories = {SentimentCategory.VERY_POS, SentimentCategory.POS, SentimentCategory.NEG, SentimentCategory.NEG};
        double[] expectedProbabilities = {0.76076, 0.639197, 0.52738, 0.5};


        for (int i = 0; i < categories.size(); i++) {
            SentimentCategory c = categories.get(i);
            double probability = predictions.get(i)[c.categoryToId()];
            assertEquals(expectedCategories[i], c);
            assertEquals(expectedProbabilities[i], probability, 0.01);
            System.out.println();
        }

    }

}