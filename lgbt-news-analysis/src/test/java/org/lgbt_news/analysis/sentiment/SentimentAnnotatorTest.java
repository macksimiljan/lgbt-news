package org.lgbt_news.analysis.sentiment;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
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

        String text = "The movie was great and enjoyable. I love that film. Yesterday I saw an ugly and smelly cat." +
                "Being a transsexual means facing stares every day.";


        annotator.execute(text);
        List<SentimentCategory> categories = annotator.getSentenceCategories();
        List<double[]> predictions = annotator.getSentencePredictions();
        SentimentCategory[] expectedCategories = {SentimentCategory.VERY_POS, SentimentCategory.POS, SentimentCategory.NEG, SentimentCategory.NEG};
        double[] expectedProbabilities = {0.76076, 0.639197, 0.52738, 0.5};


        List<Sentence> sentences = new Document(text).sentences();
        for (int i = 0; i < categories.size(); i++) {
            SentimentCategory c = categories.get(i);
            double[] p = predictions.get(i);
            double probability = predictions.get(i)[c.categoryToId()];
            assertEquals(expectedCategories[i], c);
            assertEquals(expectedProbabilities[i], probability, 0.01);
            System.out.println();
        }

    }

}