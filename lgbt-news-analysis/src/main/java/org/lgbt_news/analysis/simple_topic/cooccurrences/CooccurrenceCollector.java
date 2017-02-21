package org.lgbt_news.analysis.simple_topic.cooccurrences;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.lgbt_news.analysis.simple_topic.preprocess.StopWordList;
import org.lgbt_news.analysis.util.SentenceExtractor;
import org.lgbt_news.analysis.util.Window;
import org.lgbt_news.collect.utils.NytDate;

import java.sql.Connection;
import java.util.*;

/**
 * Collects all words cooccurring with a target word.
 * @author max
 */
public class CooccurrenceCollector {

    private final String TARGET;
    private final SentenceExtractor EXTRACTOR;
    private int indexTarget;
    private Map<String, CooccurrenceType> previousTypes;


    public CooccurrenceCollector(Connection connection, String targetWord) {
        this.TARGET = targetWord;
        EXTRACTOR = new SentenceExtractor(connection);
    }

    public Collection<CooccurrenceType> findCooccurrences(NytDate pubDate, int halfWindowSize) {
        Set<Window> windows = EXTRACTOR.getWindowContexts(TARGET, pubDate, halfWindowSize);
        return findCooccurrences(windows);
    }

    public Collection<CooccurrenceType> findCooccurrences(Set<Window> windows) {
        previousTypes = new HashMap<>();
        for (Window window : windows) {
            Set<Cooccurrence> cooccurrences = determineCooccurrencesInWindow(window);
            for (Cooccurrence c : cooccurrences)
                handleCooccurrence(c);
        }
        return previousTypes.values();
    }

    private Set<Cooccurrence> determineCooccurrencesInWindow(Window window) {
        List<Sentence> sentences = new Document(window.getSentencesAsText()).sentences();
        List<String> words = getWords(sentences, window.getIndexFocus());
        return getCooccurrences(words);
    }

    private List<String> getWords(List<Sentence> sentences, int targetWordSentence) {
        List<String> words = new ArrayList<>();
        StopWordList stopWordList = new StopWordList();
        for (int i = 0; i < sentences.size(); i++) {
            List<String> wordsInS = sentences.get(i).words();
            for (String w : wordsInS) {
                if (!stopWordList.isStopWord(w) && !stopWordList.isANumber(w))
                    words.add(w);
                if (i == targetWordSentence && w.equals(TARGET))
                    indexTarget = words.size() - 1;
            }
        }
        return words;
    }

    private Set<Cooccurrence> getCooccurrences(List<String> words) {
        Set<Cooccurrence> cooccurrences = new HashSet<>();
        int currentDistance = indexTarget;
        for (String word : words) {
            Cooccurrence cooccurrence = new Cooccurrence(words.get(indexTarget), word);
            cooccurrence.setDistanceInWords(Math.abs(currentDistance));
            currentDistance--;
            if (!word.equals(TARGET))
                cooccurrences.add(cooccurrence);
        }

        return  cooccurrences;
    }

    private void handleCooccurrence(Cooccurrence c) {
        String coWord = c.getCoWord();
        if (previousTypes.keySet().contains(coWord))
            addAlreadyKnownType(coWord, c.getDistanceInWords());
        else
            addNewType(coWord, c.getDistanceInWords());
    }

    private void addAlreadyKnownType(String coWord, int distance) {
        CooccurrenceType cooccurrenceType = previousTypes.get(coWord);
        cooccurrenceType.increaseCountOccurrences();
        cooccurrenceType.addDistance(distance);
        previousTypes.put(coWord, cooccurrenceType);
    }

    private void addNewType(String coWord, int distance) {
        CooccurrenceType cooccurrenceType = new CooccurrenceType(TARGET, coWord);
        cooccurrenceType.setFrequency(1);
        cooccurrenceType.addDistance(distance);
        previousTypes.put(coWord, cooccurrenceType);
    }


}
