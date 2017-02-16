package org.lgbt_news.analysis.sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author max
 */
public class SentimentAnnotator {


    private StanfordCoreNLP pipeline;

    public SentimentAnnotator() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public SentimentCategory aggregateSentimentByMajority(List<SentimentCategory> categories) {
        return null;
    }

    public List<SentimentCategory> execute(String text) {
        List<SentimentCategory> categoriesPerSentence = new ArrayList<>();

        List<CoreMap> sentences = parseSentences(text);
        for (CoreMap sentence : sentences) {
            int categoryId = categorize(sentence);
            SentimentCategory category = SentimentCategory.idToCategory(categoryId);
            categoriesPerSentence.add(category);
        }
        return categoriesPerSentence;
    }

    private List<CoreMap> parseSentences(String text) {
        Annotation annotation = pipeline.process(text);
        return annotation.get(CoreAnnotations.SentencesAnnotation.class);
    }

    private int categorize(CoreMap sentence) {
        Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        return RNNCoreAnnotations.getPredictedClass(tree);
    }
}
