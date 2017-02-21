package org.lgbt_news.analysis.simple_topic.model;

import org.junit.Test;
import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceCollector;
import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceType;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatistic;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatisticCorpus;
import org.lgbt_news.analysis.util.SentenceExtractor;
import org.lgbt_news.analysis.util.Window;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class StatisticCalculationTest {

    static final String testDocument = "Snakes are great. She loves a man. The child is lovely. The man is a transgender. " +
            "A transgender man is lovely.";

    @Test
    public void isSignificant() throws Exception {
        String file = "/home/max/git/lgbt-news/lgbt-news-analysis/src/test/resources/wordStatistics.tsv";
        String word = "transgender";
        WordStatistic wordStatistic = new WordStatisticCorpus(file);
        List<Window> windows = SentenceExtractor.extractContainingWindowsFromText(testDocument, word, 1);
        CooccurrenceCollector collector = new CooccurrenceCollector(null, word);
        Collection<CooccurrenceType> cooccurrences = collector.findCooccurrences(new HashSet<>(windows));

        StatisticCalculation calc = new StatisticCalculation(wordStatistic);

        // TODO: implement
    }

    @Test
    public void test_executeCalculationMutualInformation() throws Exception {
        String wordA = "transgender";
        String wordB = "";
    }

}