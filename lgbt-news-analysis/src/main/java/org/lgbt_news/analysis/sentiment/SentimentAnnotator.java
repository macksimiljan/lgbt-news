package org.lgbt_news.analysis.sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author max
 */
public class SentimentAnnotator {

    private List<SentimentCategory> categoriesPerSentence;
    private List<double[]> predictionsPerSentence;

    private StanfordCoreNLP pipeline;

    public SentimentAnnotator() {
        resetLists();

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public List<SentimentCategory> getCategoriesPerSentence() {
        return categoriesPerSentence;
    }

    public List<double[]> getPredictionsPerSentence() {
        return predictionsPerSentence;
    }

    public void execute(String text) {
        resetLists();
        List<CoreMap> sentences = parseSentences(text);
        for (CoreMap sentence : sentences)
            categorize(sentence);
    }

    private void resetLists() {
        categoriesPerSentence = new ArrayList<>();
        predictionsPerSentence = new ArrayList<>();
    }

    private List<CoreMap> parseSentences(String text) {
        Annotation annotation = pipeline.process(text);
        return annotation.get(CoreAnnotations.SentencesAnnotation.class);
    }

    private void categorize(CoreMap sentence) {
        Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        int categoryId = RNNCoreAnnotations.getPredictedClass(tree);
        SimpleMatrix matrix = RNNCoreAnnotations.getPredictions(tree);
        double[] predictions = matrix.getMatrix().getData();

        addCategory(categoryId);
        addPredictions(predictions);
    }

    private void addCategory(int categoryId) {
        SentimentCategory category = SentimentCategory.idToCategory(categoryId);
        categoriesPerSentence.add(category);
    }

    private void addPredictions(double[] predictions) {
        predictionsPerSentence.add(predictions);
    }

}
