package org.lgbt_news.analysis.simple_topic.cooccurrences;

import edu.stanford.nlp.simple.Document;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatistic;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatisticCorpus;

import java.util.*;

/**
 * @author max
 */
public class ContextVector {

    private final int thresholdContextWord;
    private String[] contextWords;
    private Map<String, Integer> inverseIndexContextWords;
    private final Set<List<String>> corpus;
    private final int halfWindowSize;

    private Map<String, int[]> contextVectors;

    private String currentTargetWord;
    private int[] currentContextVector;

    public ContextVector(int thresholdContextWord, Set<List<String>> corpus, int halfWindowSize) {
        this.thresholdContextWord = thresholdContextWord;
        this.corpus = corpus;
        this.halfWindowSize = halfWindowSize;
        this.contextVectors = new HashMap<>();
        loadContextWords();
    }

    private void loadContextWords() {
        WordStatistic wordStatistic = new WordStatisticCorpus(WordStatisticCorpus.getFile());
        contextWords = wordStatistic.getContextWords(thresholdContextWord);
        inverseIndexContextWords = wordStatistic.getInverseIndexContextWords(thresholdContextWord);
    }

    public int[] getContextVector(String targetWord) {
        return contextVectors.get(targetWord);
    }

    public String[] getContextWords() {
        return contextWords;
    }

    public void buildContextVector(String targetWord) {
        currentTargetWord = targetWord.toLowerCase();
        currentContextVector = new int[contextWords.length];
        for (List<String> document : corpus)
            countCooccurrencesInDocument(document);

        contextVectors.put(currentTargetWord, currentContextVector);
    }

    private void countCooccurrencesInDocument(List<String> document) {
        for (int i = 0; i < document.size(); i++) {
            String word = document.get(i);
            if (word.equalsIgnoreCase(currentTargetWord)) {
                List<String> window = buildWindow(document, i);
                countCooccurrencesInWindow(window);
            }
        }
    }

    private List<String> buildWindow(List<String> document, int i) {
        List<String> window = new ArrayList<>(2*halfWindowSize + 1);
        int indexNext = i - halfWindowSize;
        indexNext = (indexNext < 0) ? 0 : indexNext;
        while (indexNext <= i + halfWindowSize) {
            if (indexNext < document.size())
                window.add(document.get(indexNext));
            indexNext++;
        }
        return window;
    }

    private void countCooccurrencesInWindow(List<String> window) {
        for (String word : window) {
            if (word.equals(currentTargetWord))
                continue;
            else {
                if (inverseIndexContextWords.containsKey(word)) {
                    int indexCooccurringWord = inverseIndexContextWords.get(word);
                    int[] contextVector = contextVectors.get(currentTargetWord);
                    currentContextVector[indexCooccurringWord] += 1;
                }
            }
        }
    }
}
